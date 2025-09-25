<template>
	<div class="app-container">
		<el-form :model="queryParams" ref="queryForm" size="mini" :inline="true" v-show="showSearch" label-width="68px">
			<el-form-item label="店铺名称" prop="merchantId">
				<el-select v-model="queryParams.merchantId" placeholder="请选择店铺" clearable>
					<el-option v-for="(item, index) in dialogShopList" :key="index" :value="item.value"
						:label="item.label"></el-option>
				</el-select>
			</el-form-item>
			<el-form-item label="站点" prop="siteId">
				<el-select v-model="queryParams.siteId" placeholder="请选择站点" clearable>
					<el-option v-for="(item, index) in siteList" :key="index" :value="item.value"
						:label="item.label"></el-option>
				</el-select>
			</el-form-item>
			<el-form-item label="物流类型" prop="logisticType">
				<el-select v-model="queryParams.logisticType" placeholder="请选择物流类型" clearable>
					<el-option v-for="(item, index) in logTypeList" :key="index" :value="item.value"
						:label="item.label"></el-option>
				</el-select>
			</el-form-item>
			<el-form-item label="订单状态" prop="shipStatus">
				<el-select v-model="queryParams.shipStatus" placeholder="请选择状态" clearable>
					<el-option v-for="(item, index) in statusList" :key="index" :value="item.value"
						:label="item.label"></el-option>
				</el-select>
			</el-form-item>
			<el-form-item label="创建时间">
				<el-date-picker v-model="createdTime" type="daterange" range-separator="至" start-placeholder="开始日期"
					end-placeholder="结束日期" value-format="yyyy-MM-dd"
					@change="(value) => onChangeDate(value, 'create')"></el-date-picker>
			</el-form-item>
			<el-form-item>
				<el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
				<el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
			</el-form-item>
		</el-form>

		<el-row :gutter="10" class="mb8">
			<el-col :span="1.5">
				<el-button type="primary" icon="el-icon-refresh" size="mini" @click="handleAdd">同步订单
				</el-button>
				<el-button type="danger" icon="el-icon-delete" size="mini" :disabled="tableSelection.length === 0"
					@click="handleBatchDelete">批量删除</el-button>
				<el-button type="success" icon="el-icon-edit" size="mini" :disabled="tableSelection.length === 0"
					@click="handleBatchUpdate()">批量更新</el-button>
			</el-col>
			<right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
		</el-row>

		<el-table v-loading="loading" :data="shopList" :default-sort="defaultSort" @cell-click="onCellClick"
			@selection-change="handleSelectionChange" @sort-change="handleSortChange">
			<el-table-column type="selection" width="55">
			</el-table-column>

			<el-table-column label="订单ID" align="center" :show-overflow-tooltip="true">
				<template slot-scope="scope">
					<div class="click-link" @click="onClickToPage(scope.row)">{{ scope.row.itemId }}</div>
					<div>{{ scope.row.orderId }}</div>
				</template>
			</el-table-column>

			<el-table-column label="产品首图" align="center" prop="thumbnail" :show-overflow-tooltip="true">
				<template slot-scope="scope">
					<el-image style="width: 60px; height: 60px" :src="scope.row.thumbnail" :preview-src-list="srcList">
					</el-image>
				</template>
			</el-table-column>

			<el-table-column label="订单信息" align="center" :show-overflow-tooltip="true">
				<template slot-scope="scope">
					<div>{{ scope.row.unitPrice }} USD × {{ scope.row.quantity }} </div>
					<div>{{ scope.row.sellerSku }} </div>
					<div class="custom-btn" :key="item" v-for="item in scope.row.orderSkuInfo">
						<div>
							{{ item }}
						</div>
					</div>
				</template>
			</el-table-column>
			<el-table-column label="买家信息" align="center" :show-overflow-tooltip="true">
				<template slot-scope="scope">
					<img :src="siteIdhandle(scope.row.siteId).img" class="rounded-image"
						style="width: 32px;height: 24px;">
					<div style="margin-left: 0px;">{{ `${siteIdhandle(scope.row.siteId).city}` }} {{
						scope.row.logisticType === 'remote' ? '自发货' : '海外仓' }}</div>
					<div>店铺ID&nbsp;&nbsp;&nbsp;{{ scope.row.mercadoShopId }}</div>
					<div>店铺名称&nbsp;&nbsp;&nbsp;{{ scope.row.mercadoShopName }}</div>
					<div>买家姓名&nbsp;&nbsp;&nbsp;{{ scope.row.buyerNickName }}</div>
				</template>
			</el-table-column>

			<el-table-column label="下单时间" align="center" :show-overflow-tooltip="true">
				<template slot-scope="scope">
					<div>下单时间 :{{ scope.row.dateCreated }}</div>
					<div>付款时间 :{{ scope.row.dateCreated }}</div>
					<div>{{ scope.row.dateShipping !== null ? '预计到达时间 :' : '' }} {{ scope.row.dateShipping }}</div>
				</template>
			</el-table-column>
			<el-table-column label="订单状态" align="center">
				<template slot-scope="scope">
					<span>{{ scope.row.shipStatus }}</span>
				</template>
			</el-table-column>
			<el-table-column label="操作" align="center" prop="createdTime">
				<template slot-scope="scope">
					<p><el-button type="danger" icon="el-icon-delete" @click="handleDelete(scope.row)"
							size="mini">删除</el-button></p>
					<p><el-button type="primary" icon="el-icon-edit" @click="handleUpdate(scope.row)"
							size="mini">更新</el-button></p>
				</template>
			</el-table-column>
		</el-table>

		<pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum"
			:limit.sync="queryParams.pageSize" @pagination="getList" />

		<!-- 更新链接 -->
		<el-dialog title="新增产品" width="25%" class="custom-dialog" custom-class="my-dialog" append-to-body
			:visible.sync="linkVisible" :close-on-click-modal="false" :close-on-press-escape="false"
			:show-close="false">
			<el-form ref="elForm" :model="formData" :rules="formRules">
				<el-form-item label="选择店铺" :prop="shopType === 'update' ? 'merchantId' : 'shopId'">
					<template v-if="shopType === 'update'">
						<el-select v-model="formData.merchantId" style="width: 100%;" placeholder="请选择店铺" clearable
							filterable>
							<el-option v-for="(item, index) in dialogShopList" :key="index" :value="item.value"
								:label="item.label"></el-option>
						</el-select>
					</template>
					<template v-else>
						<el-select v-model="formData.shopId" style="width: 100%;" placeholder="请选择店铺" clearable
							filterable>
							<el-option v-for="(item, index) in dialogShopList" :key="index" :value="item.id"
								:label="item.label"></el-option>
						</el-select>
					</template>
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
import { listOrderInfo, selectListShop, deleteItem, updateShopOrder, batchUpdate, collectLink } from "@/api/mercado/order"

export default {
	name: "Item",
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
			// 采集链接弹出框
			linkVisible: false,
			// 查询参数
			createdTime: [],
			updatedTime: [],
			queryParams: {
				pageNum: 1,
				pageSize: 10,
				merchantId: undefined,
				siteId: undefined,
				logisticType: undefined,
				status: undefined
			},
			// 表单参数
			form: {},
			// 表单校验
			rules: {
				code: [
					{ required: true, message: "密钥不能为空", trigger: "blur" }
				]
			},
			srcList: [],
			formData: {
				merchantId: '',
				shopId: ''
			},
			formRules: {
				merchantId: [{ required: true, message: '请选择店铺', trigger: ['blur', 'change'] }],
				shopId: [{ required: true, message: '请选择店铺', trigger: ['blur', 'change'] }]
			},
			tableSelection: [],
			dialogShopList: [],
			// 默认排序
			defaultSort: { prop: 'operTime', order: 'descending' },
			siteList: [
				{ value: 'MLM', label: '墨西哥' },
				{ value: 'MLB', label: '巴西' },
				{ value: 'MLC', label: '智利' },
				{ value: 'MCO', label: '哥伦比亚' }
			],
			logTypeList: [
				{ value: 'remote', label: '自发货' },
				{ value: 'fulfillment', label: '海外仓' }
			],
			statusList: [
				{ value: 'cancelled', label: 'cancelled' },
				{ value: 'delivered', label: 'delivered' },
				{ value: 'not_delivered', label: 'not_delivered' },
				{ value: 'pending', label: 'pending' },
				{ value: 'ready_to_ship', label: 'ready_to_ship' },
				{ value: 'shipped', label: 'shipped' }
			],
			shopType: 'update'
		};
	},
	created() {
		this.getList();
		this.getShopList()
	},
	methods: {
		// 采集链接
		onCollect() {
			this.shopType = 'collect'
			this.linkVisible = true
		},
		// 批量更新

		// 跳转链接
		onClickToPage(row) {
			window.open(row.permalink)
		},
		/** 排序触发事件 */
		handleSortChange(column, prop, order) {
			this.queryParams.orderByColumn = column.prop;
			this.queryParams.isAsc = column.order;
			this.getList();
		},
		// 选择日期
		onChangeDate(val, type) { },
		// 表格多选
		handleSelectionChange(val) {
			this.tableSelection = val.map((v) => v.orderId)
		},
		// 查看图片
		onCellClick(row, column) {
			if (column.property === 'thumbnail') {
				let arr = []
				arr.push(row.thumbnail)
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
					let { merchantId, shopId } = this.formData
					if (this.shopType === 'update') {
						updateShopOrder(merchantId).then((res) => {
							if (res.code == 200) {
								this.$message.success(res.msg)
								this.getList()
							} else {
								this.$message.error(res.msg)
							}
						})
					} else {
						let data = {
							shopId: shopId,
							linkIdList: this.tableSelection
						}
						collectLink(data).then((res) => {
							if (res.code === 200) {
								this.$message.success(res.msg)
								this.getList()
							} else this.$message.error(res.msg)
						})
					}
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
			listOrderInfo(data).then(response => {
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
							id: v.id,
							value: v.merchantId,
							label: v.mercadoShopName
						}
					})
				} else {
					this.dialogShopList = []
				}
			})
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
			this.shopType = 'update'
		},
		/** 删除按钮操作 */
		handleDelete(row) {
			const id = row.orderId;
			this.$modal.confirm('是否确认删除？').then(function () {
				return deleteItem(id);
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
				return deleteItem(ids);
			}).then(() => {
				this.getList();
				this.$modal.msgSuccess("删除成功");
			}).catch(() => {
			});
		},
		// 单个更新链接
		handleUpdate(row) {
			const id = row.orderId;
			this.$modal.confirm('是否确认更新？').then(function () {
				return batchUpdate(id);
			}).then(() => {
				this.getList();
				this.$modal.msgSuccess("更新成功!");
			}).catch(() => {
			});
		},
		// 批量删除
		handleBatchUpdate() {
			let ids = this.tableSelection.join(',')
			this.$modal.confirm('是否确认更新选中的产品？').then(function () {
				return batchUpdate(ids);
			}).then(() => {
				this.getList();
				this.$modal.msgSuccess("批量更新成功!");
			}).catch(() => {
			});
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

.button-container {
	display: flex;
	flex-wrap: wrap;
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