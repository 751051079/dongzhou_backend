<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="批次名称" prop="codeName">
        <el-input v-model="queryParams.codeName" placeholder="请输入批次名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="批次类型" prop="noticeType">
        <el-select v-model="queryParams.codeType" placeholder="批次类型" clearable>
          <el-option v-for="dict in dict.type.sys_upc_type" :key="dict.value" :label="dict.label" :value="dict.value" />
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
          v-hasPermi="['system:upc:add']">新增
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>
    <el-table v-loading="loading" :data="upcManageList">
      <el-table-column label="公司名称" align="center" prop="deptName" :show-overflow-tooltip="true" />
      <el-table-column label="批次名称" align="center" prop="codeName" :show-overflow-tooltip="true" />
      <el-table-column label="号码类型" align="center" prop="noticeType" width="100">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_upc_type" :value="scope.row.codeType" />
        </template>
      </el-table-column>
      <el-table-column label="总数" align="center" prop="totalNum" :show-overflow-tooltip="true" />
      <el-table-column label="可用" align="center" prop="availableNum" :show-overflow-tooltip="true" />
      <el-table-column label="已用" align="center" prop="usedNum" :show-overflow-tooltip="true" />
      <el-table-column label="创建者" align="center" prop="createName" width="100" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="100">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" prop="createdTime" width="320px">
        <template slot-scope="scope">
          <el-button type="danger" icon="el-icon-delete" @click="removeUpcInfo(scope.row)" size="mini">删除</el-button>
          <el-button type="primary" icon="el-icon-edit" @click="editUpcInfo(scope.row)" size="mini">更新</el-button>
          <el-button type="primary" @click="getUpcInfo(scope.row)" size="mini">查看</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />
    <br>
    <el-dialog v-bind="$attrs" v-on="$listeners" @close="onClose" :visible.sync="open" :title="title">
      <el-row :gutter="5">
        <el-form ref="formAdd" :model="form" :rules="rules" size="medium" label-width="300px">
          <el-col :span="13">
            <el-form-item label="批次名称" prop="codeName">
              <el-input v-model="form.codeName" placeholder="请输入批次名称" :maxlength="11" clearable
                :style="{ width: '100%' }"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="13">
            <el-form-item label="批次类型" prop="codeType">
              <el-select v-model="form.codeType" placeholder="请输入批次类型" clearable :style="{ width: '100%' }">
                <el-option v-for="dict in dict.type.sys_upc_type" :key="dict.value" :label="dict.label"
                  :value="dict.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="13">
            <el-form-item label="号码编号" prop="upcContent">
              <el-input v-model="form.upcContent" type="textarea" placeholder="请输入号码编号" show-word-limit
                :autosize="{ minRows: 1, maxRows: 10000 }" :style="{ width: '100%' }"></el-input>
            </el-form-item>
          </el-col>
        </el-form>
      </el-row>
      <div slot="footer">
        <el-button @click="cancel">取消</el-button>
        <el-button type="primary" @click="handleConfirm">确定</el-button>
      </div>
    </el-dialog>
    <el-dialog v-bind="$attrs" v-on="$listeners" :visible.sync="open2" title="UPC修改">
      <el-row :gutter="5">
        <el-form ref="formEdit" :model="formEdit" :rules="rules2" size="medium" label-width="300px">
          <el-col :span="13">
            <el-form-item label="批次名称" prop="codeName">
              <el-input v-model="formEdit.codeName" placeholder="请输入批次名称" :maxlength="11" clearable
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
    <el-dialog v-bind="$attrs" v-on="$listeners" @close="onCloseNew" :visible.sync="open1" :title="title1">
      <el-form :model="upcQueryParams" ref="queryForm1" size="small" :inline="true" label-width="68px">
        <el-form-item label="UPC" prop="upcCode">
          <el-input v-model="upcQueryParams.upcCode" placeholder="请输入UPC" clearable @keyup.enter.native="handleQuery" />
        </el-form-item>
        <el-form-item label="状态" prop="upcStatus">
          <el-select v-model="upcQueryParams.upcStatus" placeholder="状态" clearable>
            <el-option v-for="dict in dict.type.sys_upc_use_type" :key="dict.value" :label="dict.label"
              :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery1">搜索</el-button>
          <el-button icon="el-icon-refresh" size="mini" @click="resetQuery1">重置</el-button>
        </el-form-item>
      </el-form>
      <el-table v-loading="loading1" :data="upcList">
        <el-table-column label="UPC" align="center" prop="upcCode" :show-overflow-tooltip="true" />
        <el-table-column label="状态" align="center" prop="upcStatus" :show-overflow-tooltip="true" />
      </el-table>

      <pagination v-show="total1 > 0" :total="total1" :page.sync="upcQueryParams.pageNum"
        :limit.sync="upcQueryParams.pageSize" @pagination="getList1" />
    </el-dialog>
  </div>
</template>

<script>
import { listUpcManage, addUpc, listUpc, getUpcInfo, editUpcInfo, removeUpcInfo } from "@/api/mercado/upc";

export default {
  name: "Upc",
  dicts: ['sys_upc_type', 'sys_upc_use_type'],
  data() {
    return {
      // 遮罩层
      loading: true,
      loading1: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      total1: 0,
      // upc批次列表数据
      upcManageList: [],
      upcList: [],
      // 弹出层标题
      title: "号码批次导入",
      // 是否显示弹出层
      open: false,
      open1: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        codeType: null,
        codeName: null
      },
      upcQueryParams: {
        pageNum: 1,
        pageSize: 10,
        upcCode: null,
        upcStatus: null,
        upcId: null
      },
      // 表单参数
      form: {
        codeName: null,
        codeType: null,
        upcContent: null
      },
      title1: 'UPC',
      // 表单校验
      rules: {
        codeName: [
          { required: true, message: "号码池批次名称不能为空", trigger: "blur" }
        ],
        codeType: [
          { required: true, message: "号码池批次类型", trigger: "change" }
        ],
        upcContent: [
          { required: true, message: "号码不能为空", trigger: "blur" }
        ]
      },
      rules2: {
        codeName: [
          { required: true, message: "号码池批次名称不能为空", trigger: "blur" }
        ]
      },
      formEdit: {},
      open2: false
    };
  },
  created() {
    this.getList();
  },
  methods: {
    getList() {
      this.loading = true;
      listUpcManage(this.queryParams).then(response => {
        this.upcManageList = response.rows;
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
        codeName: undefined,
        codeType: undefined
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
      this.title = "批量导入号码";
    },
    getUpcInfo(row) {
      this.upcQueryParams.upcId = row.id;
      this.open1 = true;
      this.getList1();
    },
    onClose() {
      this.$refs['formAdd'].resetFields()
    },
    handleConfirm(data) {
      this.$refs["formAdd"].validate(valid => {
        if (valid) {
          addUpc(this.form).then(response => {
            this.$modal.msgSuccess("导入成功");
            this.open = false;
            this.getList();
            this.reset();
          });
        }
      })
    },
    /** 搜索按钮操作 */
    handleQuery1() {
      this.upcQueryParams.pageNum = 1;
      this.getList1();
    },
    /** 重置按钮操作 */
    resetQuery1() {
      this.resetForm("queryForm1");
      this.handleQuery1();
    },
    getList1() {
      this.loading1 = true;
      listUpc(this.upcQueryParams).then(response => {
        this.upcList = response.rows;
        this.total1 = response.total;
        this.loading1 = false;
      });
    },
    onCloseNew() {
      this.$refs['queryForm1'].resetFields()
      this.upcQueryParams.pageNum = 1;
    },
    editUpcInfo(data) {
      this.open2 = true;
      this.formEdit = { id: data.id, codeName: data.codeName };
    },
    cancel2() {
      this.open2 = false;
      this.formEdit = {};
    },
    handleConfirm2() {
      editUpcInfo(this.formEdit).then(res => {
        if (res.code === 200) {
          this.getList();
          this.open2 = false;
          this.$modal.msgSuccess("修改成功");
        } else {
          this.$modal.msgError(res.msg);
        }
      })
    },
    removeUpcInfo(row) {
      this.$modal.confirm('是否确认删除？').then(function () {
        return removeUpcInfo(row.id);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {
      });
    }
  }
};
</script>
