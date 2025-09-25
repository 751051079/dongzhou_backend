<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="mini" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="店铺ID">
        <el-input v-model="queryParams.merchantId" placeholder="请输入店铺ID" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="创建人">
        <el-input v-model="queryParams.createdName" placeholder="请输入创建人" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="创建时间">
        <el-date-picker v-model="createdTime" type="daterange" range-separator="至" start-placeholder="开始日期"
          end-placeholder="结束日期" value-format="yyyy-MM-dd"
          @change="(value) => onChangeDate(value, 'create')"></el-date-picker>
      </el-form-item>
      <el-form-item label="更新时间">
        <el-date-picker v-model="updatedTime" type="daterange" range-separator="至" start-placeholder="开始日期"
          end-placeholder="结束日期" value-format="yyyy-MM-dd"
          @change="(value) => onChangeDate(value, 'update')"></el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" icon="el-icon-plus" size="mini" v-hasPermi="['system:shop:add']"
          :disabled="tableSelection.length === 0" @click="handleAdd">采集链接
        </el-button>
        <el-button type="primary" plain icon="el-icon-download" size="mini" @click="onCrawling">爬取链接
        </el-button>
        <el-button type="primary" icon="el-icon-plus" size="mini" @click="onAddLink">新增链接
        </el-button>
        <el-button type="danger" icon="el-icon-delete" size="mini" :disabled="tableSelection.length === 0"
          @click="handleBatchDelete">批量删除</el-button>
        <el-button type="info" plain icon="el-icon-upload2" size="mini" @click="handleImport"
          v-hasPermi="['system:user:import']">导入</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="shopList" :default-sort="defaultSort" @cell-click="onCellClick"
      @selection-change="handleSelectionChange" @sort-change="handleSortChange">
      <el-table-column type="selection" width="55">
      </el-table-column>
      <el-table-column label="产品信息" align="left" :show-overflow-tooltip="true" width="420px">
        <template slot-scope="scope">
          <div class="click-link" @click="onClickToPage(scope.row)">{{ scope.row.itemId }}</div>
          <div>售价:{{ scope.row.price }} {{ scope.row.currencyId }}</div>
          <div>{{ scope.row.title }}</div>
        </template>
      </el-table-column>
      <el-table-column label="产品首图" align="center" prop="pictureUrl" :show-overflow-tooltip="true">
        <template slot-scope="scope">
          <el-image style="width: 60px; height: 60px" :src="scope.row.pictureUrl" :preview-src-list="srcList">
          </el-image>
        </template>
      </el-table-column>
      <el-table-column label="产品详情" align="left" :show-overflow-tooltip="true">
        <template slot-scope="scope">
          <div>店铺ID&nbsp;&nbsp;&nbsp;{{ scope.row.merchantId }}</div>
          <div>类目ID&nbsp;&nbsp;&nbsp;{{ scope.row.categoryId }}</div>
          <div>类别ID&nbsp;&nbsp;&nbsp;{{ scope.row.domainId }}</div>
        </template>
      </el-table-column>
      <el-table-column label="站点信息" align="center" :show-overflow-tooltip="true">
        <template slot-scope="scope">
          <img :src="siteIdhandle(scope.row.siteId).img" class="rounded-image" style="width: 32px;height: 24px;">
          <div style="margin-left: 0px;">
            {{ `${siteIdhandle(scope.row.siteId).city}` }}
          </div>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createdTime" sortable="custom"
        :sort-orders="['descending', 'ascending']">
        <template slot-scope="scope">
          <span>{{ scope.row.createdTime }}</span>
        </template>
      </el-table-column>

      <el-table-column label="更新时间" align="center" prop="updatedTime" sortable="custom"
        :sort-orders="['descending', 'ascending']">
        <template slot-scope="scope">
          <span>{{ scope.row.updatedTime }}</span>
        </template>
      </el-table-column>

      <el-table-column label="系统创建时间" align="center" prop="systemCreateTime" sortable="custom"
        :sort-orders="['descending', 'ascending']">
      </el-table-column>

      <el-table-column label="创建人" align="center" prop="createdName" sortable="custom"
        :sort-orders="['descending', 'ascending']">
      </el-table-column>

      <el-table-column label="操作" align="center" prop="createdTime" width="120px">
        <template slot-scope="scope">
          <el-button type="danger" icon="el-icon-delete" @click="handleDelete(scope.row)" size="mini">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改套餐对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="780px" append-to-body v-loading="loading2">
      <!--      <el-link type="danger" @click="openBing()">绑定流程查看</el-link>-->
      ---------------------------------------
      <el-link type="primary"
        href="https://global-selling.mercadolibre.com/authorization?response_type=code&client_id=XXXXXXXXX&redirect_uri=https://你的域名/mercado/shop"
        target="_blank">去绑定店铺</el-link>
      <!--      <el-form ref="form" :model="form" :rules="rules" size="medium" label-width="100px" label-position="left">-->
      <!--        <el-form-item label="密钥" prop="code">-->
      <!--          <el-input v-model="form.code" placeholder="请输入密钥" show-word-limit clearable-->
      <!--                     :style="{width: '100%'}"></el-input>-->
      <!--        </el-form-item>-->
      <!--        <el-form-item label="备注" prop="remark">-->
      <!--          <el-input v-model="form.remark" type="textarea" placeholder="备注" :maxlength="500"-->
      <!--                    show-word-limit :autosize="{minRows: 4, maxRows: 4}" :style="{width: '100%'}"></el-input>-->
      <!--        </el-form-item>-->
      <!--      </el-form>-->
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 采集链接 -->
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

    <!-- 新增链接 -->
    <el-dialog class="custom-dialog" custom-class="my-dialog" width="30%" append-to-body :visible.sync="visibleAddLink"
      :close-on-click-modal="false" :close-on-press-escape="false" :show-close="false">
      <el-form ref="elformRef" :model="linkFormData" :rules="formRules">
        <el-form-item label="链接地址" prop="productUrl">
          <el-input v-model="linkFormData['productUrl']" type="textarea" :autosize="{ minRows: 3 }"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="closeAddLink">取 消</el-button>
        <el-button type="primary" @click="confirmAddLink">确 定</el-button>
      </template>
    </el-dialog>

    <!--导入对话框 -->
    <el-dialog :title="upload.title" :visible.sync="upload.open" width="400px" append-to-body>
      <!-- :action="upload.url + '?updateSupport=' + upload.updateSupport" -->
      <el-upload ref="upload" accept=".xlsx, .xls" :limit="1" :headers="upload.headers" :action="upload.url"
        :disabled="upload.isUploading" :on-progress="handleFileUploadProgress" :on-success="handleFileSuccess"
        :auto-upload="false" drag>
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <div class="el-upload__tip text-center" slot="tip">
          <!-- <div class="el-upload__tip" slot="tip">
            <el-checkbox v-model="upload.updateSupport" /> 是否更新已经存在的用户数据
          </div> -->
          <span>仅允许导入xls、xlsx格式文件。</span>
          <!-- <el-link type="primary" :underline="false" style="font-size:12px;vertical-align: baseline;" @click="importTemplate">下载模板</el-link> -->
        </div>
      </el-upload>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitFileForm">确 定</el-button>
        <el-button @click="upload.open = false">取 消</el-button>
      </div>
    </el-dialog>
    <!--    <el-dialog title="绑定流程查看" :visible.sync="open2" >-->
    <!--      <h3>点击"去绑定店铺",然后输入美客多登陆信息,确认后等待页面跳转到如下显示,按下图红框复制密钥即可,将密钥填入密钥框即可完成店铺绑定</h3>-->
    <!--      <img src="@/assets/images/bingShop.png" width="100%">-->
    <!--    </el-dialog>-->
  </div>
</template>

<script>
import { listShop, addShop, deleteShop, selectListShop, addLink, crawling, addLink2 } from "@/api/mercado/link";
import { getToken } from "@/utils/auth";

export default {
  name: "Shop",
  dicts: [],
  data() {
    return {
      visibleAddLink: false,
      linkFormData: {
        productUrl: ''
      },
      // 遮罩层
      loading: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 公告表格数据
      shopList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 采集链接弹出框
      linkVisible: false,
      // 爬取链接
      crawlingVisible: false,
      // 查询参数
      createdTime: [],
      updatedTime: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        merchantId: undefined,
        createdName: undefined,
        createBeginTime: undefined,
        createEndTime: undefined,
        updateBeginTime: undefined,
        updateEndTime: undefined
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
      srcList: [],
      formData: {
        shopId: ''
      },
      formRules: {
        shopId: [{ required: true, message: '请选择店铺', trigger: ['blur', 'change'] }],
        productUrl: [
          { required: true, message: '请输入链接地址', trigger: ['blur', 'change'] }
        ]
      },
      tableSelection: [],
      dialogShopList: [],
      // 导入参数
      upload: {
        // 是否显示弹出层（用户导入）
        open: false,
        // 弹出层标题（用户导入）
        title: "",
        // 是否禁用上传
        isUploading: false,
        // 是否更新已经存在的用户数据
        updateSupport: 0,
        // 设置上传的请求头部
        headers: { Authorization: "Bearer " + getToken() },
        // 上传的地址
        url: process.env.VUE_APP_BASE_API + "/order/link/importData"
      },
      // 默认排序
      defaultSort: { prop: 'operTime', order: 'descending' },
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
    this.getShopList()
  },
  methods: {
    // 跳转链接
    onClickToPage(row) {
      window.open(row.productUrl)
    },
    /** 排序触发事件 */
    handleSortChange(column, prop, order) {
      this.queryParams.orderByColumn = column.prop;
      this.queryParams.isAsc = column.order;
      this.getList();
    },
    // 打开新增链接弹窗
    onAddLink() {
      this.visibleAddLink = true
    },
    // 关闭新增链接弹窗
    closeAddLink() {
      this.visibleAddLink = false,
        this.$refs['elformRef'].resetFields()
    },
    // 提交新增链接
    confirmAddLink() {
      this.$refs['elformRef'].validate((valid) => {
        if (valid) {
          let data = {
            ...this.linkFormData
          }
          addLink2(data).then((res) => {
            if (res.code == 200) {
              this.$message.success(res.msg)
              this.getList()
              this.closeAddLink()
            } else {
              this.$message.error(res.msg)
            }
          })
        }
      })
    },
    /** 导入按钮操作 */
    handleImport() {
      this.upload.title = "链接导入";
      this.upload.open = true;
    },
    // 文件上传中处理
    handleFileUploadProgress(event, file, fileList) {
      this.upload.isUploading = true;
    },
    // 文件上传成功处理
    handleFileSuccess(response, file, fileList) {
      this.upload.open = false;
      this.upload.isUploading = false;
      this.$refs.upload.clearFiles();
      this.$alert("<div style='overflow: auto;overflow-x: hidden;max-height: 70vh;padding: 10px 20px 0;'>" + response.msg + "</div>", "导入结果", { dangerouslyUseHTMLString: true });
      this.getList();
    },
    // 提交上传文件
    submitFileForm() {
      this.$refs.upload.submit();
    },
    // 爬取链接
    onCrawling() {
      this.$prompt('请输入店铺链接', '', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPattern: /^(?:http(s)?:\/\/)?[\w.-]+(?:\.[\w\.-]+)+[\w\-\._~:/?#[\]@!\$&'\(\)\*\+,;=.]+$/,
        inputErrorMessage: '链接格式不正确'
      }).then(({ value }) => {
        crawling({ shopUrl: value }).then((res) => {
          if (res.code == 200) {
            this.$message.success(res.msg)
            this.getList()
          } else {
            this.$message.error(res.msg)
          }
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '取消输入'
        });
      });
    },
    // 选择日期
    onChangeDate(val, type) { },
    // 表格多选
    handleSelectionChange(val) {
      this.tableSelection = val.map((v) => v.id)
    },
    // 查看图片
    onCellClick(row, column) {
      if (column.property === 'pictureUrl') {
        let arr = []
        arr.push(row.pictureUrl)
        this.srcList = arr
      }
    },
    // 关闭选择店铺弹窗
    onClose() {
      this.linkVisible = false
      this.$refs['elForm'].resetFields()
    },
    // 选择店铺提交
    onSubmit() {
      this.$refs['elForm'].validate((valid) => {
        if (valid) {
          let data = {
            ...this.formData,
            linkIdList: this.tableSelection
          }
          addLink(data).then((res) => {
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
    /** 查询公告列表 */
    getList() {
      let data = {
        ...this.queryParams,
        createBeginTime: this.createdTime && this.createdTime.length > 0 ?
          this.createdTime[0] : undefined,
        createEndTime: this.createdTime && this.createdTime.length > 0 ?
          this.createdTime[1] : undefined,
        updateBeginTime: this.updatedTime && this.updatedTime.length > 0 ?
          this.updatedTime[0] : undefined,
        updateEndTime: this.updatedTime && this.updatedTime.length > 0 ?
          this.updatedTime[1] : undefined,
      }
      this.loading = true;
      listShop(data).then(response => {
        this.shopList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
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
      this.createdTime = []
      this.updatedTime = []
      this.resetForm("queryForm");
      this.handleQuery();
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
    /** 新增按钮操作 */
    handleAdd() {
      this.linkVisible = true
         },
    /** 提交按钮 */
    submitForm: function () {
      // this.loading2 = true;
      // this.$refs["form"].validate(valid => {
      //   if (valid) {
      //     addShop(this.form).then(response => {
      //       this.$modal.msgSuccess("绑定成功");
      //       this.getList();
      //     }).finally(() => {
      //       this.loading2 = false;
      //     })
      //   }
      // });
      this.open = false;
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
    // 批量删除
    handleBatchDelete() {
      let ids = this.tableSelection.join(',')
      this.$modal.confirm('是否确认删除选中的产品？').then(function () {
        return deleteShop(ids);
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
</style>

<style>
.my-dialog {
  top: 40%;
  transform: translateY(-40%);
}

.rounded-image {
  width: auto;
  /* 你可以根据需要调整宽度 */
  height: auto;
  border-radius: 5px;
  /* 调整这个值来改变圆角的程度 */
}
</style>
