<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="店铺名称" prop="mercadoShopName">
        <el-input
          v-model="queryParams.mercadoShopName"
          placeholder="请输入店铺名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="店铺别名" prop="aliasName">
        <el-input
          v-model="queryParams.aliasName"
          placeholder="请输入店铺别名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['system:shop:add']"
        >新增
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="shopList">
      <el-table-column
        label="公司名称"
        align="center"
        prop="deptName"
        :show-overflow-tooltip="true"
      />
      <el-table-column
        label="店铺名称"
        align="center"
        prop="mercadoShopName"
        :show-overflow-tooltip="true"
      />
      <el-table-column
        label="店铺别名"
        align="center"
        prop="aliasName"
        :show-overflow-tooltip="true"
      />
      <el-table-column
        label="商家id"
        align="center"
        prop="merchantId"
        :show-overflow-tooltip="true"
      />
      <el-table-column
        label="状态"
        align="center"
        prop="authorizeStatus"
        :show-overflow-tooltip="true"
      />
      <el-table-column label="授权时间" align="center" prop="authorizeTime">
        <template slot-scope="scope">
          <span>{{ scope.row.authorizeTime}}</span>
        </template>
      </el-table-column>
      <el-table-column
        label="备注"
        align="remark"
        prop="remark"
        :show-overflow-tooltip="true"
      />
      <el-table-column
        label="创建人"
        align="createName"
        prop="createName"
        :show-overflow-tooltip="true"
      />
      <el-table-column label="创建时间" align="center" prop="createdTime">
        <template slot-scope="scope">
          <span>{{scope.row.createTime}}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:shop:remove']"
          >删除
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="reBingSshop(scope.row)"
            v-hasPermi="['system:shop:add']"
          >重新绑定
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改套餐对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="780px" append-to-body v-loading="loading2">
      <el-link type="danger" @click="openBing()">绑定流程查看</el-link>
      ---------------------------------------
      <el-link type="primary" href="https://global-selling.mercadolibre.com/authorization?response_type=code&client_id=2271900076226075&redirect_uri=https://58.248.136.140:8080/mel/redirect" target="_blank">去绑定店铺</el-link>
      <el-form ref="form" :model="form" :rules="rules" size="medium" label-width="100px" label-position="left">
        <el-form-item label="密钥" prop="code">
          <el-input v-model="form.code" placeholder="请输入密钥" show-word-limit clearable
                    :style="{width: '100%'}"></el-input>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="备注" :maxlength="500"
                    show-word-limit :autosize="{minRows: 4, maxRows: 4}" :style="{width: '100%'}"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
    <el-dialog title="绑定流程查看" :visible.sync="open2" >
      <h3>点击"去绑定店铺",然后输入美客多登陆信息,确认后等待页面跳转到如下显示,按下图红框复制密钥即可,将密钥填入密钥框即可完成店铺绑定</h3>
      <img src="@/assets/images/bingShop.png" width="100%">
    </el-dialog>
  </div>
</template>

<script>
  import {listShop, addShop, deleteShop} from "@/api/mercado/shop";

  export default {
    name: "Shop",
    dicts: [],
    data() {
      return {
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
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 10,
          mercadoShopName: undefined,
          aliasName: undefined
        },
        // 表单参数
        form: {},
        // 表单校验
        rules: {
          code: [
            {required: true, message: "密钥不能为空", trigger: "blur"}
          ]
        },
        loading2: false,
        open2: false
      };
    },
    created() {
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
        this.form = {
          code: null,
          remark: null
        };
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
      /** 新增按钮操作 */
      handleAdd() {
        this.reset();
        this.open = true;
        this.title = "绑定店铺";
      },
      /** 提交按钮 */
      submitForm: function () {
        this.loading2 = true;
        this.$refs["form"].validate(valid => {
          if (valid) {
            addShop(this.form).then(response => {
              this.$modal.msgSuccess("绑定成功");
              this.getList();
            }).finally(() => {
              this.loading2 = false;
            })
          }
        });
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
      openBing(){
        this.open2 = true;
      },
      reBingSshop(data){
        this.reset();
        this.open = true;
        this.title = "重新绑定";
        this.form.id = data.id
        console.log(this.form);
      }
    }
  };
</script>
