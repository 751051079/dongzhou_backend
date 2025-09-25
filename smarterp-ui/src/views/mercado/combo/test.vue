<template>
  <div>
    <el-form ref="form" :model="form" :rules="rules" size="medium" label-width="100px" label-position="left">
      <el-form-item label="套餐名称" prop="comboName">
        <el-input v-model="form.comboName" placeholder="请输入套餐名称" :maxlength="11" show-word-limit clearable
                  prefix-icon='el-icon-mobile' :style="{width: '100%'}"></el-input>
      </el-form-item>
      <el-form-item label="套餐类型" prop="comboType">
        <el-select v-model="form.comboType" placeholder="请输入套餐类型" clearable :style="{width: '100%'}">
          <el-option v-for="(item, index) in comboTypeOptions" :key="index" :label="item.label"
                     :value="item.value" :disabled="item.disabled"></el-option>
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
        <el-input v-model="form.combo_description" type="textarea" placeholder="请输入套餐描述" :maxlength="500"
                  show-word-limit :autosize="{minRows: 4, maxRows: 4}" :style="{width: '100%'}"></el-input>
      </el-form-item>
      <el-form-item size="large">
        <el-button type="primary" @click="submitForm">提交</el-button>
        <el-button @click="resetForm">重置</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>
<script>
  export default {
    components: {},
    props: [],
    data() {
      return {
        form: {
          comboName: '',
          comboType: undefined,
          maxShopCount: 1,
          maxChildAccount: 1,
          comboEfficientDays: 7,
          comboSalePrice: 500,
          collectionQuantityDays: 100,
          combo_description: undefined,
        },
        rules: {
          comboName: [{
            required: true,
            message: '请输入套餐名称',
            trigger: 'blur'
          }, {
            pattern: /^1(3|4|5|7|8|9)\d{9}$/,
            message: '手机号格式错误',
            trigger: 'blur'
          }],
          comboType: [{
            required: true,
            message: '请输入套餐类型',
            trigger: 'change'
          }, {
            pattern: /^1(3|4|5|7|8|9)\d{9}$/,
            message: '手机号格式错误',
            trigger: 'change'
          }],
          maxShopCount: [{
            required: true,
            message: '请输入最大店铺数',
            trigger: 'blur'
          }],
          maxChildAccount: [{
            required: true,
            message: '请输入账号数量',
            trigger: 'blur'
          }],
          comboEfficientDays: [{
            required: true,
            message: '请输入有效天数',
            trigger: 'blur'
          }],
          comboSalePrice: [{
            required: true,
            message: '请输入套餐价格',
            trigger: 'blur'
          }],
          collectionQuantityDays: [{
            required: true,
            message: '请输入采集数量',
            trigger: 'blur'
          }],
          combo_description: [],
        },
        comboTypeOptions: [{
          "label": "选项一",
          "value": 1
        }, {
          "label": "选项二",
          "value": 2
        }],
      }
    },
    computed: {},
    watch: {},
    created() {},
    mounted() {},
    methods: {
      submitForm() {
        this.$refs['form'].validate(valid => {
          if (!valid) return
          // TODO 提交表单
        })
      },
      resetForm() {
        this.$refs['form'].resetFields()
      },
    }
  }

</script>
<style>
</style>
