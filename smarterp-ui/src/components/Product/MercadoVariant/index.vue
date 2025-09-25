<template>
  <div>
    <el-card shadow="hover">
      <h5>{{colorId}}:</h5>
      <el-tag
        :key="tag"
        v-for="tag in colorTags"
        closable
        :disable-transitions="false"
        @close="handleClose(tag,colorTags)">
        {{tag}}
      </el-tag>
      <el-input
        class="input-new-tag"
        v-if="colorInputVisible"
        v-model="colorInputValue"
        ref="saveColorTagInput"
        size="small"
        @keyup.enter.native="handleInputConfirm"
        @blur="handleInputConfirm()"
      >
      </el-input>
      <el-button v-else class="button-new-tag" size="small" @click="showColorInput">添加颜色</el-button>
    </el-card>
    <el-card shadow="hover" v-if="sizeTags.length > 0">
      <h5>{{sizeId}}:</h5>
      <el-tag
        :key="tag"
        v-for="tag in sizeTags"
        closable
        :disable-transitions="false"
        @close="handleClose(tag,sizeTags)">
        {{tag}}
      </el-tag>
      <el-input
        class="input-new-tag"
        v-if="sizeInputVisible"
        v-model="sizeInputValue"
        ref="saveSizeTagInput"
        size="small"
        @keyup.enter.native="handleSizeConfirm"
        @blur="handleSizeConfirm"
      >
      </el-input>
      <el-button v-else class="button-new-tag" size="small" @click="showSizeInput">添加尺码</el-button>
    </el-card>
    <el-card shadow="hover">
      <h5>sku:</h5>
      <el-tag
        :key="tag"
        v-for="tag in skuTags"
        closable
        :disable-transitions="false"
        @close="handleClose(tag,skuTags)">
        {{tag}}
      </el-tag>

      <el-button class="button-new-tag" size="small" @click="refSku">重置</el-button>
    </el-card>
  </div>
</template>

<script>
  import {uploadMercadoApi} from "@/api/mercado/product";

  export default {
    name: "MercadoVariant",
    components: {},
    props: {
      skuPre: {
        type: String,
        default: 'PRE',
      },
      colorTags: {
        type: Array,
        default: () => [],
      },
      sizeTags: {
        type: Array,
        default: () => [],
      },
      colorId: '',
      sizeId: ''
    },
    data() {
      return {
        // colorTags: ['red', 'green', 'yellow'],
        // sizeTags: ['EU36', 'EU37', 'EU38'],
        colorInputVisible: false,
        colorInputValue: '',
        sizeInputVisible: false,
        sizeInputValue: '',
        skuTags: []
      };
    },
    created() {

    },
    watch: {
      skuPre(pre){
        let skus = [];
        this.colorTags.forEach(item1 => {
          if (this.sizeTags.length === 0){
            skus.push(this.skuPre + '_'  + item1);
          }
          this.sizeTags.forEach(item2 => {
            skus.push(pre + '_' + item1 + '_' + item2);
          })
        });
        this.skuTags = skus;
      },
      colorTags: {
        immediate: true, // 首次加载的时候执行函数
        deep: true, // 深入观察,监听数组值，对象属性值的变化
        handler(colors) {
          let skus = [];
          colors.forEach(item1 => {
            if (this.sizeTags.length === 0){
              skus.push(this.skuPre + '_'  + item1);
            }
            this.sizeTags.forEach(item2 => {
              skus.push(this.skuPre + '_' + item1 + '_' + item2);
            })
          });
          this.skuTags = skus;
        },
      },
      sizeTags: {
        immediate: true, // 首次加载的时候执行函数
        deep: true, // 深入观察,监听数组值，对象属性值的变化
        handler(sizes) {
          let skus = [];
          if (sizes.length > 0){
            sizes.forEach(item1 => {
              this.colorTags.forEach(item2 => {
                skus.push(this.skuPre + '_' + item2 + '_' + item1);
              })
            });
            this.skuTags = skus;
          }

          //this.$emit('changeSkuListEvent', this.skuTags);
        }
      },
      skuTags: {
        immediate: false, // 首次加载的时候执行函数
        deep: false, // 深入观察,监听数组值，对象属性值的变化
        handler(skus,oldskus) {
          if (oldskus.length !== 0){
            //console.log(oldskus);
            this.$emit('changeSkuListEvent', skus);
          }
        }
      }
    },
    computed: {
      // skuTags(){
      //   let skus = [];
      //   this.colorTags.forEach(item1 =>{
      //     this.sizeTags.forEach(item2 =>{
      //       skus.push(this.skuPre + '_' + item1 + '_' + item2);
      //     })
      //   })
      //   return skus;
      // }
    }
    ,
    methods: {
      handleClose(tag, tags) {
        if (tags.length > 1){
          tags.splice(tags.indexOf(tag), 1);
        }else{
          this.$modal.msgError("最少保留一个");
        }
      }
      ,
      showColorInput() {
        this.colorInputVisible = true;
        this.$nextTick(_ => {
          this.$refs.saveColorTagInput.$refs.input.focus();
        });
      }
      ,
      handleInputConfirm() {
        let colorInputValue = this.colorInputValue;
        if (colorInputValue && !this.colorTags.includes(colorInputValue)) {
          this.colorTags.push(colorInputValue);
        }else{
          this.$modal.msgError("不能重复")
        }
        this.colorInputVisible = false;
        this.colorInputValue = '';
      }
      ,
      showSizeInput() {
        this.sizeInputVisible = true;
        this.$nextTick(_ => {
          this.$refs.saveSizeTagInput.$refs.input.focus();
        });
      }
      ,
      handleSizeConfirm() {
        let sizeInputValue = this.sizeInputValue;
        if (sizeInputValue && !this.sizeTags.includes(sizeInputValue)) {
          this.sizeTags.push(sizeInputValue);
          //console.log(111)

        }else{
          this.$modal.msgError("不能重复")
        }
        this.sizeInputVisible = false;
        this.sizeInputValue = '';
      }
      ,
      refSku() {
        this.colorTags.push(null);
        this.colorTags.splice(this.colorTags.length -1, 1);
      }
    }
  }
  ;
</script>
<style>
  .el-tag + .el-tag {
    margin-left: 10px;
  }

  .button-new-tag {
    margin-left: 10px;
    height: 32px;
    line-height: 30px;
    padding-top: 0;
    padding-bottom: 0;
  }

  .input-new-tag {
    width: 90px;
    margin-left: 10px;
    vertical-align: bottom;
  }
</style>
