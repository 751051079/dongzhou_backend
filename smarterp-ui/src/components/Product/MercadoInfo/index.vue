<template>
  <div>
    <el-form :inline="true" class="demo-form-inline">
      <el-form-item :label="info.name + ':'" :key="info.name" v-for="info in infoList">
        <el-input key="autoButtont" v-model="info.value" :disabled="showCloseHandler(info.id)"></el-input>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
  import {uploadMercadoApi} from "@/api/mercado/product";

  export default {
    name: "MercadoInfo",
    components: {},
    props: {
      infoList: {
        type: Array,
        default: () => [],
      }
    },
    data() {
      return {};
    },
    created() {

    },
    watch: {
      infoList: {
        immediate: true, // 首次加载的时候执行函数
        deep: true, // 深入观察,监听数组值，对象属性值的变化
        handler(infos) {
          this.$emit('getProductInfo', infos);
        },
      }
    },
    computed: {}
    ,
    methods: {
      //如果是这些属性则不展示
      showCloseHandler(data){
        if ('GTIN' === data || 'SELLER_SKU' === data || 'PACKAGE_WEIGHT' === data ||
          'PACKAGE_LENGTH' === data || 'PACKAGE_WIDTH' === data || 'PACKAGE_HEIGHT' === data){
          return true;
        }
      }
    }
  };
</script>
