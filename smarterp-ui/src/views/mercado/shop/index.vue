<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="店铺名称" prop="mercadoShopName">
        <el-input v-model="queryParams.mercadoShopName" placeholder="请输入店铺名称" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="店铺别名" prop="aliasName">
        <el-input v-model="queryParams.aliasName" placeholder="请输入店铺别名" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
          v-hasPermi="['system:shop:add']">绑定店铺
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="shopList">
      <el-table-column label="公司名称" align="center" prop="deptName" :show-overflow-tooltip="true" />
      <el-table-column label="店铺名称" align="center" prop="mercadoShopName" :show-overflow-tooltip="true" />
      <el-table-column label="店铺别名" align="center" prop="aliasName" :show-overflow-tooltip="true" />
      <el-table-column label="商家id" align="center" prop="merchantId" :show-overflow-tooltip="true" />
      <el-table-column label="状态" align="center" prop="authorizeStatus" :show-overflow-tooltip="true" />
      <el-table-column label="价格系数" align="center" prop="priceRatio" :show-overflow-tooltip="true" />
      <el-table-column label="创建人" align="createName" prop="createName" :show-overflow-tooltip="true" />
      <el-table-column label="创建时间" align="center" prop="createdTime">
        <template slot-scope="scope">
          <span>{{ scope.row.createTime }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="320px">
        <template slot-scope="scope">
        <!-- <el-button type="danger" icon="el-icon-delete" @click="handleDelete(scope.row)" size="mini">删除</el-button> -->
          <el-button type="primary" @click="getShopInfoSite(scope.row)" size="mini">查看站点</el-button>
          <el-button type="success" icon="el-icon-edit" @click="editPriceRatio(scope.row)" size="mini">价格系数</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />
    <el-dialog :title="title" :visible.sync="open" width="780px" append-to-body v-loading="loading2">
      <el-link type="primary"
        href="https://global-selling.mercadolibre.com/authorization?response_type=code&client_id=XXXXXXXXX&redirect_uri=https://你的域名/mercado/shop"
        target="_blank">去绑定店铺</el-link>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
    <el-dialog v-bind="$attrs" v-on="$listeners" @close="onCloseNew" :visible.sync="open1" :title="title1">
      <el-form :model="siteQueryParams" ref="queryForm1" size="small" :inline="true" label-width="68px">
      </el-form>
      <el-table v-loading="loading1" :data="shopInfoSiteList">
        <el-table-column label="站点ID" align="center" prop="id" :show-overflow-tooltip="true" />
        <el-table-column label="商家ID" align="center" prop="merchantId" :show-overflow-tooltip="true" />
        <el-table-column label="站点信息" align="center" :show-overflow-tooltip="true">
          <template slot-scope="scope">
            <img :src="siteIdhandle(scope.row.siteId).img" class="rounded-image" style="width: 32px;height: 24px;">
          </template>
        </el-table-column>
        <el-table-column label="发货模式" align="center" prop="logisticType" :show-overflow-tooltip="true" />
        <el-table-column label="限制产品数" align="center" prop="quota" :show-overflow-tooltip="true" />
        <el-table-column label="当前产品数" align="center" prop="totalItems" :show-overflow-tooltip="true" />
      </el-table>
      <pagination v-show="total1 > 0" :total="total1" :page.sync="siteQueryParams.pageNum"
        :limit.sync="siteQueryParams.pageSize" @pagination="getList1" />
    </el-dialog>

    <el-dialog v-bind="$attrs" v-on="$listeners" :visible.sync="open2" title="" width="500px">
      <el-row :gutter="5">
        <el-form ref="formEdit" :model="formEdit" :rules="rules2" size="medium" label-width="100px">
          <el-col :span="13">
            <el-form-item label="价格系数" prop="priceRatio">
              <el-input v-model="formEdit.priceRatio" placeholder="请输入价格系数" :maxlength="8" clearable
                :style="{ width: '100%' }"></el-input>
            </el-form-item>
          </el-col>
        </el-form>
      </el-row>
      <div slot="footer">
        <el-button @click="cancel2">取消</el-button>
        <el-button type="primary" @click="handleConfirm2">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listShop, addShop, deleteShop, getShopInfoSite, listShopInfoSite, editPriceRatio } from "@/api/mercado/shop";

export default {
  name: "Shop",
  dicts: [],
  data() {
    return {
      // 遮罩层
      loading: true,
      loading1: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 公告表格数据
      shopList: [],
      shopInfoSiteList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      open1: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        mercadoShopName: undefined,
        aliasName: undefined
      },
      siteQueryParams: {
        pageNum: 1,
        pageSize: 10
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        code: [
          { required: true, message: "密钥不能为空", trigger: "blur" }
        ]
      },
      loading2: false,
      open2: false,
      formEdit: {}
    };
  },
  created() {
    //判断url是否携带code参数
    //获取路由参数
    const query = this.$route.query;
    if (query.code) {
      //如果存在code则调用后端接口
      let data = { code: query.code };
      if (this.form.id) {
        data.id = this.form.id
      }
      addShop(data).then(response => {
        this.$modal.msgSuccess("绑定成功");
        this.form = {};
        this.$router.push('/mercado/shop')
        this.getList();
      })
    }
    this.getList();
  },
  methods: {
    /** 查询公告列表 */
    getList() {
      this.loading = true;
      listShop(this.queryParams).then(response => {
        this.shopList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {};
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    getShopInfoSite(row) {
      this.siteQueryParams.merchantId = row.merchantId;
      this.open1 = true;
      this.getList1();
    },
    editPriceRatio(data) {
      this.open2 = true;
      this.formEdit = { deptId: data.deptId, priceRatio: data.priceRatio };
    },
    cancel2() {
      this.open2 = false;
      this.formEdit = {};
    },
    handleConfirm2() {
      editPriceRatio(this.formEdit).then(res => {
        if (res.code === 200) {
          this.getList();
          this.open2 = false;
          this.$modal.msgSuccess("修改成功");
        } else {
          this.$modal.msgError(res.msg);
        }
      })
    },
    /** 新增按钮操作 */
    handleAdd() {
      window.open("https://global-selling.mercadolibre.com/authorization?response_type=code&client_id=XXXXXXXXX&redirect_uri=https://你的域名/mercado/shop");
      this.getList();
    },
    siteIdhandle(data) {
      if (data === 'MLM') {
        return {
          city: 'Mexico',
          img: 'https://ml.damaishuju.com/static/mx.bc63d25b.svg'

        }
      }
      if (data === 'MLB') {
        return {
          city: 'Brazil',
          img: 'https://ml.damaishuju.com/static/br.5ec13287.svg'
        }
      }
      if (data === 'MLC') {
        return {
          city: 'Chile',
          img: 'https://ml.damaishuju.com/static/cl.b9ff305a.svg'
        }
      }
      if (data === 'MCO') {
        return {
          city: 'Colombia',
          img: 'https://ml.damaishuju.com/static/co.59ec93f7.svg'
        }
      }
    },
    /** 提交按钮 */
    submitForm: function () {
      this.open = false;
    },
    getList1() {
      this.loading1 = true;
      listShopInfoSite(this.siteQueryParams).then(response => {
        this.shopInfoSiteList = response.rows;
        this.total1 = response.total;
        this.loading1 = false;
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const id = row.id;
      this.$modal.confirm('是否确认删除？').then(function () {
        return deleteShop(id);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {
      });
    },
    openBing() {
      this.open2 = true;
    },
    reBingSshop(data) {
      this.reset();
      this.open = true;
      this.title = "重新绑定";
      this.form.id = data.id
      console.log(this.form);
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
</style>
