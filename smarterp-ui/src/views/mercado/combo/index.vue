<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="套餐名称" prop="comboName">
        <el-input v-model="queryParams.comboName" placeholder="请输入套餐名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="套餐类型" prop="comboType">
        <el-select v-model="queryParams.comboType" placeholder="套餐类型" clearable>
          <el-option v-for="dict in dict.type.sys_combo_type" :key="dict.value" :label="dict.label"
            :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
          v-hasPermi="['system:combo:add']">新增</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="comboList">
      <el-table-column label="套餐名称" align="center" prop="comboName" :show-overflow-tooltip="true" />
      <el-table-column label="套餐类型" align="center" prop="comboType" width="100">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_combo_type" :value="scope.row.comboType" />
        </template>
      </el-table-column>
      <el-table-column label="店铺数量" align="center" prop="maxShopCount" :show-overflow-tooltip="true" />
      <el-table-column label="账号数量" align="center" prop="maxChildAccount" :show-overflow-tooltip="true" />
      <el-table-column label="有效天数" align="center" prop="comboEfficientDays">
        <template slot-scope="scope">
          <span>{{ scope.row.comboEfficientDays + '天' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="价格" align="center" prop="comboSalePrice">
        <template slot-scope="scope">
          <span>{{ scope.row.comboSalePrice + '元' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="采集数/日" align="center" prop="collectionQuantityDays">
        <template slot-scope="scope">
          <span>{{ scope.row.collectionQuantityDays + '个' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="套餐说明" align="center" prop="comboDescription" :show-overflow-tooltip="true" />
      <el-table-column label="创建者" align="center" prop="createName" width="100" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="100">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="更新人" align="center" prop="updateName" width="100" />
      <el-table-column label="更新时间" align="center" prop="updateTime" width="100">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.updateTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" prop="createdTime" width="220px">
				<template slot-scope="scope">
					<el-button type="danger" icon="el-icon-delete" @click="handleDelete(scope.row)"
						size="mini">删除</el-button>
					<el-button type="primary" icon="el-icon-edit" @click="handleUpdate(scope.row)"
						size="mini">更新</el-button>
				</template>
			</el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改套餐对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="780px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" size="medium" label-width="100px" label-position="left">
        <el-form-item label="套餐名称" prop="comboName">
          <el-input v-model="form.comboName" placeholder="请输入套餐名称" :maxlength="11" show-word-limit clearable
            prefix-icon='el-icon-mobile' :style="{ width: '40%' }"></el-input>
        </el-form-item>
        <el-form-item label="套餐类型" prop="comboType">
          <el-select v-model="form.comboType" placeholder="套餐类型" clearable :style="{ width: '40%' }">
            <el-option v-for="dict in dict.type.sys_combo_type" :key="dict.value" :label="dict.label"
              :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="最大店铺数" prop="maxShopCount">
          <el-input-number v-model="form.maxShopCount" placeholder="请输入最大店铺数" step-strictly></el-input-number>
        </el-form-item>
        <el-form-item label="账号数量" prop="maxChildAccount">
          <el-input-number v-model="form.maxChildAccount" placeholder="请输入账号数量" step-strictly></el-input-number>
        </el-form-item>
        <el-form-item label="有效天数" prop="comboEfficientDays">
          <el-input-number v-model="form.comboEfficientDays" placeholder="请输入有效天数" step-strictly>
          </el-input-number>
        </el-form-item>
        <el-form-item label="套餐价格" prop="comboSalePrice">
          <el-input-number v-model="form.comboSalePrice" placeholder="请输入套餐价格" step-strictly></el-input-number>
        </el-form-item>
        <el-form-item label="采集数量/日" prop="collectionQuantityDays">
          <el-input-number v-model="form.collectionQuantityDays" placeholder="请输入采集数量" step-strictly>
          </el-input-number>
        </el-form-item>
        <el-form-item label="套餐描述" prop="combo_description">
          <el-input v-model="form.comboDescription" type="textarea" placeholder="请输入套餐描述" :maxlength="500"
            show-word-limit :autosize="{ minRows: 4, maxRows: 4 }" :style="{ width: '100%' }"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listCombo, getComboInfo, addCombo, editCombo, deleteCombo } from "@/api/mercado/combo";

export default {
  name: "Combo",
  dicts: ['sys_combo_type'],
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 公告表格数据
      comboList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        comboTitle: undefined,
        createBy: undefined,
        status: undefined
      },
      // 表单参数
      form: {

      },
      // 表单校验
      rules: {
        comboName: [
          { required: true, message: "套餐名称不能为空", trigger: "blur" }
        ],
        comboType: [
          { required: true, message: "套餐类型不能为空", trigger: "change" }
        ],
        maxShopCount: [
          { required: true, message: "最大店铺数不能为空", trigger: "blur" }
        ],
        maxChildAccount: [
          { required: true, message: "账号数量不能为空", trigger: "blur" }
        ],
        comboEfficientDays: [
          { required: true, message: "有效天数不能为空", trigger: "blur" }
        ],
        comboSalePrice: [
          { required: true, message: "套餐价格不能为空", trigger: "blur" }
        ],
        collectionQuantityDays: [
          { required: true, message: "采集数量/日不能为空", trigger: "blur" }
        ]
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询公告列表 */
    getList() {
      this.loading = true;
      listCombo(this.queryParams).then(response => {
        this.comboList = response.rows;
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
        id: undefined,
        comboName: null,
        comboType: undefined,
        maxShopCount: undefined,
        maxChildAccount: undefined,
        comboEfficientDays: undefined,
        comboSalePrice: undefined,
        collectionQuantityDays: undefined,
        comboDescription: null
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
      this.title = "添加套餐";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id;
      getComboInfo(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改套餐";
      });
    },
    /** 提交按钮 */
    submitForm: function () {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != undefined) {
            editCombo(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addCombo(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const id = row.id;
      this.$modal.confirm('是否确认删除？').then(function () {
        return deleteCombo(id);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {
      });
    }
  }
};
</script>
