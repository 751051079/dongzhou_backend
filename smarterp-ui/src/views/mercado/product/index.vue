<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="产品标题" prop="productTitle">
        <el-input v-model="queryParams.productTitle" placeholder="请输入产品标题" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="店铺名称" prop="merchantId">
        <el-select v-model="queryParams.merchantId" placeholder="请选择店铺名称" clearable>
          <el-option v-for="(item, index) in dialogShopList" :key="index" :value="item.value"
            :label="item.label"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="发布状态" prop="releaseStatus">
        <el-select v-model="queryParams.releaseStatus" placeholder="状态" clearable>
          <el-option v-for="dict in releaseStatusType" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd">产品采集
        </el-button>
        <el-button type="danger" icon="el-icon-delete" size="mini" :disabled="selectionList.length === 0"
          @click="onBatchDelete">批量删除</el-button>
        <el-button type="warning" size="mini" :disabled="selectionList.length === 0"
          @click="onBatchSendProduct">批量发布</el-button>
        <el-button type="primary" size="mini" :disabled="selectionList.length === 0" @click="onCopy">
          复制到
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

   
    <el-table v-loading="loading" :data="productList" @cell-click="onCellClick" class="content"
      @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="50px">
      </el-table-column>

      <el-table-column label="产品首图" align="center" prop="pictureUrl">
        <template slot-scope="scope">
          <el-image style="width: 80px; height: 80px" :src="scope.row.pictureUrl" :preview-src-list="srcList">
          </el-image>
        </template>
      </el-table-column>
      <el-table-column label="产品信息" align="left" prop="title" width="370px" :show-overflow-tooltip="true">
        <template slot-scope="scope">
          <el-button v-show="scope.row.infringementStatus === '可能侵权'" type="warning" size="mini"
            @click="onViewInfringementInfo(scope.row.infringementInfo)">可能侵权</el-button>
            <el-button v-show="scope.row.infringementStatus === '不会侵权'" type="success" size="mini"
            @click="onViewInfringementInfo(scope.row.infringementInfo)">不会侵权</el-button>
            <el-button v-show="scope.row.infringementStatus === '一定侵权'" type="danger" size="mini"
            @click="onViewInfringementInfo(scope.row.infringementInfo)">一定侵权</el-button>
          <div class="click-link" @click="onClickToPage(scope.row.mercadoProductUrl)">{{ scope.row.cbtItemId }}</div>
          <div>{{ scope.row.title }}</div>
         
        </template>
      </el-table-column>
     
      <el-table-column label="尺寸和重量" align="left" prop="title" :show-overflow-tooltip="true">
        <template slot-scope="scope">
          <div>长&nbsp;&nbsp;&nbsp;{{ scope.row.packageLength }}</div>
          <div>宽&nbsp;&nbsp;&nbsp;{{ scope.row.packageWidth }}</div>
          <div>高&nbsp;&nbsp;&nbsp;{{ scope.row.packageHeight }}</div>
          <div>重量&nbsp;&nbsp;&nbsp;{{ scope.row.packageWeight }}</div>
        </template>
      </el-table-column>

      <el-table-column label="店铺信息" align="left" prop="title" :show-overflow-tooltip="true" width="220px">
        <template slot-scope="scope">
          <div>店铺名称&nbsp;&nbsp;&nbsp;{{ scope.row.shopName }}</div>
          <div>UPC编码&nbsp;&nbsp;&nbsp;{{ scope.row.upcCode }}</div>
          <div>SKU前缀&nbsp;&nbsp;&nbsp;{{ scope.row.skuPre }}</div>
        </template>
      </el-table-column>

      <el-table-column label="状态" align="left" prop="releaseStatus" :show-overflow-tooltip="true">
        <template slot-scope="scope">
          <el-button key="autoButtonq" v-show="scope.row.releaseStatus === 'ReleaseSuccess'" type="success"
            size="mini">发布成功</el-button>
          <el-button v-show="scope.row.releaseStatus === 'ReleaseFail'" type="danger" size="mini"
            @click="onViewErrorLog(scope.row)">发布失败</el-button>
          <el-button v-show="scope.row.releaseStatus === 'NoRelease'" type="info" size="mini" disabled>未发布</el-button>
          <el-button v-show="scope.row.releaseStatus === 'ReleaseIng'" type="primary" size="mini" disabled
            :loading="true">发布中</el-button>
        </template>
      </el-table-column>
      <el-table-column label="站点信息" align="left" width="330px">
        <template slot-scope="scope">
          <div class="custom-btn" :key="item.id" v-for="item in scope.row.mercadoProductItemList">
            <div style="display: flex;align-items: center;">
              <img :src="siteIdhandle(item.siteId).img" class="rounded-image" style="width: 32px;height: 24px;">
              <div style="margin-left: 20px;">
                {{ `${siteIdhandle(item.siteId).city}` + `${item.logisticType === 'fulfillment' ? '(Full)' : ''}` }}
                {{ item.siteSalePrice + 'USD' }}
              </div>
            </div>
            <div>
              <el-button key="autoButtonw" v-show="item.siteReleaseStatus === 'ReleaseSuccess'" type="success" size="mini" @click="onClickToPage(item.permalink)">发布成功</el-button>
              <el-button key="autoButtonr"
                v-show="(scope.row.releaseStatus === 'NoRelease' || scope.row.releaseStatus === 'ReleaseFail') && item.siteReleaseStatus === 'NoRelease'"
                type="info" disabled size="mini">未发布</el-button>
              <el-button key="autoButtone" v-show="(scope.row.releaseStatus === 'ReleaseSuccess' && item.siteReleaseStatus === 'NoRelease') ||
                item.siteReleaseStatus === 'ReleaseIng'" :loading="true" disabled type="primary"
                size="mini">发布中</el-button>
              <el-button v-show="item.siteReleaseStatus === 'ReleaseFail'" type="danger" size="mini"
                @click="onViewErrorLog(item)">发布失败</el-button>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="创建信息" align="left" prop="createdTime">
        <template slot-scope="scope">
          <div><span>{{ scope.row.createName }}</span></div>
          <div><span>{{ scope.row.createTime }}</span></div>
        </template>
      </el-table-column>
  

     
      <el-table-column label="操作" align="left" prop="createdTime">
        <template slot-scope="scope">
          <p><el-button v-if="scope.row.releaseStatus === 'ReleaseSuccess'" type="primary"
            @click="queryProduct(scope.row)" size="mini">查看</el-button></p>
          <p> <el-button v-if="'ReleaseSuccess' !== scope.row.releaseStatus " type="danger" @click="deleteProduct(scope.row)"
            size="mini">删除</el-button></p>
          <p> <el-button v-if="scope.row.releaseStatus === 'ReleaseFail' || scope.row.releaseStatus === 'NoRelease'"
            type="primary" @click="editProduct(scope.row)" size="mini">编辑</el-button></p>
            <p>
              <el-button v-if="scope.row.releaseStatus === 'NoRelease'" type="primary" plain size="mini"
              @click="onClickSendProduct(scope.row)">发布</el-button>
            </p>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

  
    <!-- 添加或修改套餐对话框 -->

    <!-- 发布失败错误日志 -->
    <el-dialog width="30%" :visible.sync="errorShow" :show-close="false">
      <pre style="white-space: wrap;">{{ errorStr }}</pre>
      <template #footer>
        <el-button size="mini" @click="errorShow = false">关 闭</el-button>
      </template>
    </el-dialog>

     <!-- 查看侵权信息 -->
     <el-dialog width="30%" :visible.sync="infringementInfoShow" :show-close="false">
      <pre style="white-space: wrap;">{{ infringementInfo }}</pre>
      <template #footer>
        <el-button size="mini" @click="infringementInfoShow = false">关 闭</el-button>
      </template>
    </el-dialog>

    <!-- 复制到 -->
    <el-dialog title="新增产品" width="25%" class="custom-dialog" custom-class="my-dialog" append-to-body
      :visible.sync="linkVisible" :close-on-click-modal="false" :close-on-press-escape="false" :show-close="false">
      <el-form ref="elForm" :model="formData" :rules="formRules">
        <el-form-item label="选择店铺" prop="shopId">
          <el-select v-model="formData.shopId" style="width: 100%;" placeholder="请选择店铺" clearable filterable>
            <el-option v-for="(item, index) in dialogShopList" :key="index" :value="item.value"
              :label="item.label"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="onClose">取 消</el-button>
        <el-button type="primary" @click="onSubmit">确 定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import {
  getProductList,
  sendProductOrSiteOrMsg,
  deleteProductInfoById,
  batchProduct,
  bacthSendProduct,
  getErrorLog,
  copyTo,
  selectListShop
} from "@/api/mercado/product";
export default {
  name: "Product",
  dicts: [],
  data() {
    return {
      linkVisible: false,
      // 错误日志
      errorShow: false,
       // 查看侵权信息
       infringementInfoShow: false,
      // 遮罩层
      loading: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 公告表格数据
      productList: [],
      releaseStatusType: [
        { label: '未发布', value: 'NoRelease' },
        { label: '发布成功', value: 'ReleaseSuccess' },
        { label: '发布失败', value: 'ReleaseFail' }
      ],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        productTitle: null,
        // cbtProId: null,
        // skuPre: null,
        merchantId: null,
        releaseStatus: null
      },
      srcList: [],
      selectionList: [],
      errorStr: [],
      infringementInfo: [],
      dialogShopList: [],
      formData: {
        shopId: ''
      },
      formRules: {
        shopId: [{ required: true, message: '请选择店铺', trigger: ['blur', 'change'] }]
      }
    };
  },
  created() {
    this.getList();
    this.getShopList()
  },
  methods: {
    // 跳转链接
    onClickToPage(row) {
      console.log(row)
      window.open(row)
    },
    onCopy() {
      this.linkVisible = true
    },
    // 选择 {店铺提交
    onSubmit() {
      this.$refs['elForm'].validate((valid) => {
        if (valid) {
          let data = {
            ...this.formData,
            productIds: this.selectionList
          }
          copyTo(data).then((res) => {
            if (res.code == 200) {
              this.$message.success(res.msg)
              this.getList()
            } else {
              this.$message.error(res.msg)
            }
          })
          this.onClose()
        }
      })
    },
    // 关闭选择店铺弹窗
    onClose() {
      this.linkVisible = false
      this.$refs['elForm'].resetFields()
    },
    // 查询店铺信息
    getShopList() {
      selectListShop({ pageNum: 1, pageSize: 9999 }).then((res) => {
        if (res && res.rows && res.rows.length > 0) {
          this.dialogShopList = res.rows.map((v) => {
            return {
              value: v.id,
              label: v.mercadoShopName
            }
          })
        } else {
          this.dialogShopList = []
        }
      })
    },
    // 查看日志
    onViewErrorLog(row) {
      let id = row.id ? row.id : row.itemId
      getErrorLog(id).then((res) => {
        if (res.code === 200) {
          this.errorShow = true
          this.$nextTick(() => {
            this.errorStr = res.data
          })
        } else {
          this.$message.error(res.msg)
        }
      })
    },
     // 查看侵权信息
     onViewInfringementInfo(row) {
      this.infringementInfo = '';
      this.infringementInfoShow = true;
      this.$nextTick(() => {
            this.infringementInfo = row
          })
    },
    // 发布产品
    onClickSendProduct(row) {
      bacthSendProduct(row.id).then((res) => {
        if (res.code === 200) {
          this.$message.success(res.msg)
          this.getList()
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    // 批量发布
    onBatchSendProduct() {
      let ids = this.selectionList.join(',')
      this.$modal.confirm('是否确认发布选中的产品？').then(function () {
        return bacthSendProduct(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("批量发布成功,请稍后刷新页面!");
      }).catch(() => {
      });
      // bacthSendProduct(ids).then((res) => {
      //   if (res.code === 200) {
      //     this.$message.success(res.msg)
      //     this.getList()
      //   } else {
      //     this.$message.error(res.msg)
      //   }
      // })
    },
    // 表格和多选
    handleSelectionChange(val) {
      this.selectionList = val.map((v) => v.id)
    },
    // 批量删除
    onBatchDelete() {
      let ids = this.selectionList.join(',')
      this.$modal.confirm('是否确认删除选中的产品？').then(function () {
        return batchProduct(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {
      });
      // batchProduct(ids).then((res) => {
      //   if (res.code === 200) {
      //     this.$message.success(res.msg)
      //     this.getList()
      //   } else {
      //     this.$message.error(res.msg)
      //   }
      // })
    },
    // 查看图片
    onCellClick(row, column) {
      if (column.property === 'pictureUrl') {
        let arr = []
        arr.push(row.pictureUrl)
        this.srcList = arr
      }
    },
    getList() {
      this.loading = true;
      getProductList(this.queryParams).then(response => {
        this.productList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      // this.handleQuery(data);
      this.handleQuery();
    },
    handleAdd(data) {
      //console.log(this.$router);
      this.$router.push('/mercado/product-add/index')
    },
    siteIdhandle(data) {
      if (data === 'MLM') {
        // return '墨西哥';
        return {
          city: 'Mexico',
          //  img: 'https://http2.mlstatic.com/storage/cbt-statics/layout/header/flags/Mexico.svg'
          img: 'https://ml.damaishuju.com/static/mx.bc63d25b.svg'

        }
      }
      if (data === 'MLB') {
        // return '巴西';
        return {
          city: 'Brazil',
          // img: 'https://http2.mlstatic.com/storage/cbt-statics/layout/header/flags/Brazil.svg'
          img: 'https://ml.damaishuju.com/static/br.5ec13287.svg'
        }
      }
      if (data === 'MLC') {
        // return '智利';
        return {
          city: 'Chile',
          //img: 'https://http2.mlstatic.com/storage/cbt-statics/layout/header/flags/Chile.svg'
          img: 'https://ml.damaishuju.com/static/cl.b9ff305a.svg'
        }
      }
      if (data === 'MCO') {
        // return '哥伦比亚';
        return {
          city: 'Colombia',
          // img: 'https://http2.mlstatic.com/storage/cbt-statics/layout/header/flags/Colombia.svg'
          img: 'https://ml.damaishuju.com/static/co.59ec93f7.svg'
        }
      }
      if (data === 'MLMFULL') {
        // return '墨西哥full';
        return {
          city: 'Mexico(Full)',
          // img: 'https://http2.mlstatic.com/storage/cbt-statics/layout/header/flags/Mexico.svg'
          img: 'https://ml.damaishuju.com/static/mx.bc63d25b.svg'
        }
      }
    },
    siteHandle(data) {
      if (data === 'NoRelease') {
        return '未发布';
      }
      if (data === 'ReleaseSuccess') {
        return '<el-button size="mini">超小按钮</el-button>';
      }
      if (data === 'ReleaseFail') {
        return '发布失败';
      }
    },
    desHandle(data) {
      if (data === 'Success') {
        return '成功';
      }
      if (data === 'Fail') {
        return '失败';
      }
    },
    sendProduct(data) {
      sendProductOrSiteOrMsg({ productId: data.id, releaseType: 'cbt' }).then(res => {
        this.getList();
      })
    },
    sendSite(data, item) {
      sendProductOrSiteOrMsg({ productId: data.id, releaseType: 'site', siteId: item.itemId }).then(res => {
        this.getList();
      })
    },
    sendMsg(data, item) {
      sendProductOrSiteOrMsg({ productId: data.id, releaseType: 'siteDes', siteId: item.itemId }).then(res => {
        this.getList();
      })
    },
    queryProduct(data) {
      this.$router.push({
        path: '/mercado/product-add/index', query: {
          id: data.id,
          type: 'query'
        }
      })
    },
    editProduct(data) {
      this.$router.push({
        path: '/mercado/product-add/index', query: {
          id: data.id,
          type: 'edit'
        }
      })
    },
    deleteProduct(data) {
      this.$modal.confirm('是否确认删除？').then(function () {
        return deleteProductInfoById(data.id);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => { });
    }
  }
};
</script>

<style scoped lang="scss">
.click-link {
  cursor: pointer;
  color: #1890ff;

  &:hover {
    text-decoration: underline;
  }
}

.custom-dialog {
  ::v-deep .el-dialog {
    .el-dialog__header {
      display: none;
    }
  }
}

.custom-btn {
  margin-bottom: 5px;
  display: flex;
  justify-content: space-between;

  &:last-of-type {
    margin-bottom: 0;
  }
}

.rounded-image {
  width: auto;
  /* 你可以根据需要调整宽度 */
  height: auto;
  border-radius: 5px;
  /* 调整这个值来改变圆角的程度 */
}

.content {
  margin-top: 30px;
}

/* 设置表格的外框 */
.el-table {
  border: 1px solid #ddd; /* 表格整体边框 */
  border-collapse: collapse; /* 合并表格边框 */
  width: 100%;
}

/* 设置表格头部的边框 */
.el-table th {
  border: 1px solid #ddd; /* 表头单元格边框 */
  background-color: #f2f2f2; /* 背景颜色，可以根据需要修改 */
  text-align: center; /* 文本居中 */
}

/* 设置表格内容单元格的边框 */
.el-table td {
  border: 1px solid #ddd; /* 表格内容单元格边框 */
  text-align: center; /* 文本居中 */
}

/* 如果需要设置行间隔，增加表格的行高 */
.el-table .el-table__row {
  height: 40px; /* 可以根据需要调整行高 */
}
</style>
