<template>
  <div class="app-container">
    <el-button type="primary" @click="returnTo">返回</el-button>
    采集地址:
    <el-input placeholder="请输入美客多商品链接" clearable v-model="mercadoUrl" style="width: 60%"></el-input>
    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
    <el-button type="primary" @click="getUrlInfo">确定</el-button>
    <div v-show="getUrlInfoShow" v-loading="loading">
      <el-divider></el-divider>
      <h3>站点信息</h3>
      <el-form ref="queryForm" size="small" :inline="true">
        <el-form-item label="店铺" prop="shopIds">
          <el-select style="width: 400px" v-model="shopIds" multiple placeholder="请选择" clearable>
            <el-option v-for="item in shops" :key="item.id" :label="item.mercadoShopName" :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="站点" prop="siteIds">
          <el-select style="width: 500px" v-model="siteIds" multiple placeholder="请选择" clearable>
            <el-option v-for="item in dict.type.mercado_sites" :key="item.value" :label="item.label"
              :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        <!--      <br>-->
        <el-form-item prop="gears" label="快递档位">
          <el-select style="width: 300px" v-model="gears" filterable placeholder="请选择" clearable>
            <el-option v-for="item in dict.type.mercado_gears" :key="item.value" :label="item.label"
              :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <el-divider></el-divider>
      <h3>站点价格</h3>
      <el-form ref="queryForm2" size="small" :inline="true">
        <el-form-item v-for="item in sitesPrice" :key="item.value" :label="item.label + '-售价'">
          <el-input
            oninput="value=value.replace(/[^\d^\.]+/g, '').replace(/^0+(\d)/, '$1').replace(/^\./, '0.').match(/^\d*(\.?\d{0,2})/g)[0] || ''"
            v-model="item.price" placeholder="USD" clearable />
        </el-form-item>
      </el-form>
      <el-divider></el-divider>
      <h3>利润计算</h3>
      <el-input
        oninput="value=value.replace(/[^\d^\.]+/g, '').replace(/^0+(\d)/, '$1').replace(/^\./, '0.').match(/^\d*(\.?\d{0,2})/g)[0] || ''"
        placeholder="请输入总成本RMB" clearable v-model="purchasingCost" style="width: 200px"></el-input>
      <br>
      <br>
      <el-table :data="sitesPrice" border style="width: 100%">
        <el-table-column prop="label" label="站点">
        </el-table-column>
        <el-table-column prop="price" label="售价">
          <template slot-scope="scope">
            <span>{{ scope.row.price }}美元</span>
          </template>
        </el-table-column>
        <el-table-column prop="profitMargin" label="利润率">
          <template slot-scope="scope">
            <span>{{ profitMarginHandle(scope.row) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="profit" label="利润">
          <template slot-scope="scope">
            <span>{{ profitHandle(scope.row) }}元</span>
          </template>
        </el-table-column>
      </el-table>
      <el-divider></el-divider>
      <h3>英语标题</h3>
      <el-input placeholder="请输入英语标题" maxlength="60" show-word-limit clearable v-model="englishTitle"
        style="width: 80%"></el-input>
      <h3>西语标题</h3>
      <el-input placeholder="请输入西语标题" maxlength="60" show-word-limit clearable v-model="spanishTitle"
        style="width: 80%"></el-input>
      <h3>葡语标题</h3>
      <el-input placeholder="请输入葡语标题" maxlength="60" show-word-limit clearable v-model="portugueseTitle"
        style="width: 80%"></el-input>
      <el-divider></el-divider>
      <h3>西语描述</h3>
      <el-input placeholder="请输入西语说明" :autosize="{ minRows: 10 }" type="textarea" clearable v-model="spanishMsg"
        style="width: 80%"></el-input>
      <el-divider></el-divider>
      <h3>葡语描述</h3>
      <el-input placeholder="请输入葡语说明" :autosize="{ minRows: 10 }" type="textarea" clearable v-model="portugueseMsg"
        style="width: 80%"></el-input>
      <el-divider></el-divider>
      <h3>基础属性</h3>
      <mercado-info style="width: auto;" @getProductInfo="getProductInfo" :infoList="infoList" />
      <el-divider></el-divider>
      <h3>尺码信息</h3>
      <!--尺码类别
      <el-select style="width: 300px" v-model="sizeMecdoType" filterable placeholder="请选择"
        @change="(value) => onChangeSelect(value, '1')">
        <el-option v-for="item in sizeMecdoTypes" :key="item.id" :label="item.sizeTypeName" filterable :value="item.id">
        </el-option>
      </el-select>
      尺码性别
      <el-select style="width: 300px" v-model="sizeMecdoGn" filterable placeholder="请选择"
        @change="(value) => onChangeSelect(value, '2')">
        <el-option v-for="item in sizeMecdoGns" :key="item.id" :label="item.gnsName" filterable :value="item.id">
        </el-option>
      </el-select>
      尺码表
      <el-select style="width: 300px" v-model="sizeMecdoId" filterable placeholder="请选择" clearable
        @change="getSizeInfo">
        <el-option v-for="item in sizes" :key="item.id" :label="item.name" filterable :value="item.id">
        </el-option>
      </el-select> -->

      <el-form ref="queryForm3" size="small" :inline="true">
        <el-form-item label="尺码类别">
          <el-select style="width: 300px" v-model="sizeMecdoType" filterable placeholder="请选择"
            @change="(value) => onChangeSelect(value, '1')">
            <el-option v-for="item in sizeMecdoTypes" :key="item.id" :label="item.sizeTypeName" filterable
              :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="尺码性别">
          <el-select style="width: 300px" v-model="sizeMecdoGn" filterable placeholder="请选择"
            @change="(value) => onChangeSelect(value, '2')">
            <el-option v-for="item in sizeMecdoGns" :key="item.id" :label="item.gnsName" filterable :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
        <!--      <br>-->
        <el-form-item label="尺码表">
          <el-select style="width: 300px" v-model="sizeMecdoId" filterable placeholder="请选择" clearable
            @change="getSizeInfo">
            <el-option v-for="item in sizes" :key="item.id" :label="item.name" filterable :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>

      <el-divider></el-divider>
      <h3>SKU信息</h3>
      <el-input placeholder="请输入SKU前缀" clearable v-model="skuPre" style="width: 320px"></el-input>
      <el-divider></el-divider>
      <div v-if="variant">
        <h3>变体信息</h3>
        <mercado-variant @changeSkuListEvent="getSkuList" :colorId="colorId" :colorTags="colorTags" :sizeId="sizeId"
          :sizeTags="sizeTags" :skuPre="skuPre" />
      </div>
      <el-divider></el-divider>
      <h3>图片:</h3>
      <el-button type="danger" @click="deleteImages">删除图片</el-button>
      <mercado-image @changeFileListEvent="getFileList" :colorImagesList="colorImagesList" />
      <el-divider></el-divider>
      <el-button :disabled="disableType" type="primary" @click="pubProduct">发布</el-button>
      <el-button :disabled="disableType" type="primary" @click="saveProductInfo">保存</el-button>
      <el-button :disabled="disableType" @click="emptyProductInfo">初始化</el-button>
    </div>
  </div>
</template>

<script>
import { listShop } from "@/api/mercado/shop";
import { getRateInfo } from "@/api/mercado/extApi";
import { getMercadoUrlInfo, getProductInfoById, releaseProduct, getSizeChart } from "@/api/mercado/product";
// import axios from 'axios'

export default {
  name: "ProductAdd",
  components: {},
  dicts: ['mercado_sites', 'mercado_gears', 'mercado_comm'],
  data() {
    return {
      colorImagesList: [],
      colorTags: [],
      sizeTags: [],
      verInfos: [],
      infoList: [],
      fileList: [],
      dialogVisible: false,
      extImagesUrl: null,
      skuPre: '',
      skuPreOld: null,
      englishTitle: null,
      spanishTitle: null,
      portugueseTitle: null,
      spanishMsg: null,
      portugueseMsg: null,
      shopIds: [],
      siteIds: [],
      shops: [],
      sitesPrice: [],
      purchasingCost: undefined,
      //expressageCost: undefined,
      gears: null,
      mercado_sites: [],
      mercado_gears: [],
      rate: '6.8',
      mercadoUrl: null,
      getUrlInfoShow: true,
      loading: false,
      variant: false,
      colorId: null,
      sizeId: null,
      productInfo: [],
      skuList: [],
      cpProdutId: null,
      price: undefined,
      categoryId: null,
      disableType: false,
      productId: null,
      sizes: [],
      sizeMecdoId: null,
      sizeChartInfo: {},
      sizeMecdoTypes: [
      { id: 'AMERICAN_FOOTBALL_JERSEYS', sizeTypeName: 'American_football_jerseys' },
      { id: 'BABIES_FOOTWEAR', sizeTypeName: 'Babies_footwear' },
      { id: 'BASEBALL_AND_SOFTBALL_JERSEYS', sizeTypeName: 'Baseball_and_softball_jerseys' },
      { id: 'BASKETBALL_JERSEYS', sizeTypeName: 'Basketball_jerseys' },
      { id: 'BLOUSES', sizeTypeName: 'Blouses' },
      { id: 'BOOTS_AND_BOOTIES', sizeTypeName: 'Boots_and_booties' },
      { id: 'BRAS', sizeTypeName: 'Bras' },
      { id: 'CYCLING_JERSEYS', sizeTypeName: 'Cycling_jerseys' },
      { id: 'DANCE_SNEAKERS_AND_SHOES', sizeTypeName: 'Dance_sneakers_and_shoes' },
      { id: 'DRESSES', sizeTypeName: 'Dresses' },
      { id: 'ESPADRILLES', sizeTypeName: 'Espadrilles' },
      { id: 'FASHION_KIMONOS', sizeTypeName: 'Fashion_kimonos' },
      { id: 'FISHING_PANTS', sizeTypeName: 'Fishing_pants' },
      { id: 'FISHING_SHIRTS', sizeTypeName: 'Fishing_shirts' },
      { id: 'FISHING_VESTS', sizeTypeName: 'Fishing_vests' },
      { id: 'FLATS', sizeTypeName: 'Flats' },
      { id: 'FOOTBALL_JACKETS', sizeTypeName: 'Football_jackets' },
      { id: 'FOOTBALL_SHIRTS', sizeTypeName: 'Football_shirts' },
      { id: 'FOOTBALL_SHOES', sizeTypeName: 'Football_shoes' },
      { id: 'FOOTBALL_SWEATSHIRTS_AND_HOODIES', sizeTypeName: 'Football_sweatshirts_and_hoodies' },
      { id: 'HEELS_AND_WEDGES', sizeTypeName: 'Heels_and_wedges' },
      { id: 'HIKING_BOOTS', sizeTypeName: 'Hiking_boots' },
      { id: 'JACKETS_AND_COATS', sizeTypeName: 'Jackets_and_coats' },
      { id: 'LEGGINGS', sizeTypeName: 'Leggings' },
      { id: 'LOAFERS_AND_OXFORDS', sizeTypeName: 'Loafers_and_oxfords' },
      { id: 'MOTORCYCLE_BOOTS', sizeTypeName: 'Motorcycle_boots' },
      { id: 'NEOPRENE_BOOTS', sizeTypeName: 'Neoprene_boots' },
      { id: 'NIGHTGOWNS', sizeTypeName: 'Nightgowns' },
      { id: 'PAJAMAS', sizeTypeName: 'Pajamas' },
      { id: 'PANTIES', sizeTypeName: 'Panties' },
      { id: 'PANTS', sizeTypeName: 'Pants' },
      { id: 'PONCHOS', sizeTypeName: 'Ponchos' },
      { id: 'ROBES', sizeTypeName: 'Robes' },
      { id: 'RUGBY_JERSEYS', sizeTypeName: 'Rugby_jerseys' },
      { id: 'SAFETY_FOOTWEAR', sizeTypeName: 'Safety_footwear' },
      { id: 'SANDALS_AND_CLOGS', sizeTypeName: 'Sandals_and_clogs' },
      { id: 'SCHOOL_SMOCKS', sizeTypeName: 'School_smocks' },
      { id: 'SHIRTS', sizeTypeName: 'Shirts' },
      { id: 'SHORTS', sizeTypeName: 'Shorts' },
      { id: 'SKIRTS', sizeTypeName: 'Skirts' },
      { id: 'SLIPPERS', sizeTypeName: 'Slippers' },
      { id: 'SNEAKERS', sizeTypeName: 'Sneakers' },
      { id: 'SPORT_PANTS', sizeTypeName: 'Sport_pants' },
      { id: 'SPORT_SHORTS', sizeTypeName: 'Sport_shorts' },
      { id: 'SPORT_SKIRTS', sizeTypeName: 'Sport_skirts' },
      { id: 'SPORT_T_SHIRTS', sizeTypeName: 'Sport_t_shirts' },
      { id: 'SWEATERS_AND_CARDIGANS', sizeTypeName: 'Sweaters_and_cardigans' },
      { id: 'SWEATSHIRTS_AND_HOODIES', sizeTypeName: 'Sweatshirts_and_hoodies' },
      { id: 'T_SHIRTS', sizeTypeName: 'T_shirts' },
      { id: 'TACTICAL_BOOTS', sizeTypeName: 'Tactical_boots' },
      { id: 'TACTICAL_PANTS', sizeTypeName: 'Tactical_pants' },
      { id: 'THERMAL_SHIRTS', sizeTypeName: 'Thermal_shirts' },
      { id: 'TUTUS', sizeTypeName: 'Tutus' },
      { id: 'UNDERPANTS', sizeTypeName: 'Underpants' },
      { id: 'VESTS', sizeTypeName: 'Vests' },
      { id: 'WADERS', sizeTypeName: 'Waders' },
      { id: 'WORK_PANTS', sizeTypeName: 'Work_pants' }
      ],
      sizeMecdoType: 'SNEAKERS',
      sizeMecdoGns: [
      { id: '339665', gnsName: 'Woman' },
      { id: '339666', gnsName: 'Man' },
      { id: '339668', gnsName: 'Girls' },
      { id: '339667', gnsName: 'Boys' },
      { id: '110461', gnsName: 'Gender_neutral' },
      { id: '19159491', gnsName: 'Gender_neutral_KID' },
      { id: '371795', gnsName: 'Babies' }
      ],
      sizeMecdoGn: '339665'
    };
  },
  created() {
    this.initData();
  },
  mounted() {

  },
  activated() {
    this.initData();
  },
  watch: {
    skuPre(newData, oldData) {
      if (newData.indexOf("_") !== -1) {
        this.$modal.msgError("sku前缀不能包含'_'");
        this.skuPre = oldData;
      }
    },
    shopIds(newData, data) {
      if (newData.length > 0 && newData.length !== data.length) {
        //获取最新尺码
        this.sizeMecdoId = null;
        let shopinfo = this.shops.filter(shop => newData[0] === shop.id)[0];
        if (shopinfo) {
          setTimeout(() => {
            this.getSizeChartVue(this.sizeMecdoType, this.sizeMecdoGn, shopinfo.merchantId);
          }, 1000)
        }
      }
    },
    // sizeMecdoType(data){
    //   //获取最新尺码
    //   this.sizeMecdoId = null;
    //   let shopinfo = this.shops.filter(shop => this.shopIds[0] === shop.id)[0];
    //   this.getSizeChartVue(data,this.sizeMecdoGn,shopinfo.merchantId);
    // },
    // sizeMecdoGn(data){
    //   //获取最新尺码
    //   this.sizeMecdoId = null;
    //   let shopinfo = this.shops.filter(shop => this.shopIds[0] === shop.id)[0];
    //   this.getSizeChartVue(this.sizeMecdoType,data,shopinfo.merchantId);
    // },
    mercado_sites(data) {
      data.forEach(item => {
        if ("MLM" === item.value) {
          this.siteIds = [item.value];
        }
      })
    },
    mercado_gears(data) {
      if (data.length > 0) {
        this.gears = data[1].value;
      }
    },
    siteIds: {
      handler(data) {
        let list2 = [];
        data.forEach(item => {
          this.mercado_sites.forEach(item2 => {
            if (item === item2.value) {
              list2.push({ value: item2.value, label: item2.label, price: undefined });
            }
          })
        });
        let listNew = [];
        this.sitesPrice.forEach(item => listNew.push(item));
        this.sitesPrice = list2;
        this.sitesPrice.forEach(item => {
          listNew.forEach(item2 => {
            if (item.value === item2.value) {
              item.price = item2.price;
            }
          })
        })
      }
    }
  },
  methods: {
    onChangeSelect(val, type) {
      if (type === '1') {
        //获取最新尺码
        this.sizeMecdoId = null;
        let shopinfo = this.shops.filter(shop => this.shopIds[0] === shop.id)[0];
        this.getSizeChartVue(val, this.sizeMecdoGn, shopinfo.merchantId);
      } else {
        //获取最新尺码
        this.sizeMecdoId = null;
        let shopinfo = this.shops.filter(shop => this.shopIds[0] === shop.id)[0];
        this.getSizeChartVue(this.sizeMecdoType, val, shopinfo.merchantId);
      }
    },
    getSizeList(domainId, genderId,) {

    },
    //更改尺码
    getSizeInfo(sizeMecdoId) {
      //sizes   sizeMecdoId
      //更改尺码数据
      //选择中尺码表时查询尺码表详情
      if (!sizeMecdoId || sizeMecdoId === '' || this.sizes.length <= 0) {
        return;
      }
      this.sizeChartInfo = this.sizes.filter(size => sizeMecdoId === size.id)[0];
      this.sizeTags = this.sizeChartInfo.rows.map(item => item.name);
    },
    //获取最终所有图片信息
    getFileList(data) {
      //console.log(data);
      this.fileList = data;
      //将color顺序按图片排序
      this.colorTags.splice(this.colorTags.indexOf(this.fileList[0].color), 1);
      this.colorTags.unshift(this.fileList[0].color);
    },
    //获取最终sku
    getSkuList(data) {
      this.skuList = data;
      //根据sku分割出颜色和尺寸
      let list = [];
      data.forEach(item => {
        let color = item.split("_")[1];
        list.push({ color, 'fileList': [] });
      });
      const res = new Map();
      list = list.filter((a) => !res.has(a.color) && res.set(a.color, 1));
      if (list.length > this.colorImagesList.length) {
        let colors = this.colorImagesList.map(item => item.color);
        list = list.filter(item => colors.indexOf(item.color) === -1);
        list.forEach(item => {
          this.colorImagesList.push(item);
        })
      } else if (list.length < this.colorImagesList.length) {
        let colors = list.map(item => item.color);
        this.colorImagesList = this.colorImagesList.filter(item => colors.indexOf(item.color) !== -1);
      }

      // console.log(111);
    },
    getProductInfo(data) {
      this.productInfo = data;
    },
    verifyProductDatas(type) {
      // if (!this.mercadoUrl || (this.mercadoUrl.length === 0)) {
      //   this.$modal.msgError("采集链接不能空");
      //   return;
      // }
      if (this.shopIds.length === 0) {
        this.$modal.msgError("店铺不能为空");
        return null;
      }
      if (this.siteIds.length === 0) {
        this.$modal.msgError("发布站点不能为空");
        return null;
      }
      if (!this.englishTitle || this.englishTitle.length === 0) {
        this.$modal.msgError("英文标题不能为空");
        return null;
      }
      if (this.englishTitle && this.englishTitle.length > 60) {
        this.$modal.msgError("英文标题长度不能超过60");
        return null;
      }

      if (this.spanishTitle && this.spanishTitle.length > 60) {
        this.$modal.msgError("西班牙语标题长度不能超过60");
        return null;
      }
      if (this.portugueseTitle && this.portugueseTitle.length > 60) {
        this.$modal.msgError("葡萄牙语标题长度不能超过60");
        return null;
      }


      if (this.fileList.length === 0) {
        this.$modal.msgError("产品图片不能为空");
        return null;
      }
      if (!this.skuPre || this.skuPre.length === 0) {
        this.$modal.msgError("SKU不能为空");
        return null;
      }
      if (!this.gears) {
        this.$modal.msgError("快递档位不能为空");
        return null;
      }

      let siteSM = this.siteIds.filter(item => "MLM" === item || "MLC" === item || "MCO" === item || "MLMFULL" === item);
      if (siteSM.length > 0 && (!this.spanishMsg || this.spanishMsg.length === 0)) {
        this.$modal.msgError("西语不能为空");
        return null;
      }
      siteSM = this.siteIds.filter(item => "MLB" === item);
      if (siteSM.length > 0 && (!this.portugueseMsg || this.portugueseMsg.length === 0)) {
        this.$modal.msgError("葡语不能为空");
        return null;
      }
      //判断每个颜色图片是否超过10
      let fileListNew = this.fileList.filter(item => item.fileList.length > 10);
      if (fileListNew && fileListNew.length > 0) {
        let s = '';
        fileListNew.forEach(item => {
          s = s + item.color + ',';
        });
        this.$modal.msgError(`${s}的图片不能超过10张`);
        return null;
      }
      return {
        cpProdutId: this.cpProdutId,
        price: this.price,
        categoryId: this.categoryId,
        mercadoUrl: this.mercadoUrl,
        shopIds: this.shopIds,
        siteIds: this.siteIds, gears: this.gears,
        sitesPrice: this.sitesPrice
        , englishTitle: this.englishTitle,
        spanishTitle: this.spanishTitle,
        portugueseTitle: this.portugueseTitle,
        spanishMsg: this.spanishMsg, portugueseMsg: this.portugueseMsg,
        productInfo: this.productInfo, skuList: this.skuList,
        fileList: this.fileList,
        sizeId: this.sizeId,
        colorId: this.colorId,
        skuPre: this.skuPre,
        productId: this.productId,
        sizeChartInfo: this.sizeChartInfo,
        //代表发布产品
        operateType: type
      };
    },
    pubProduct() {
      let productInfo = this.verifyProductDatas('release');
      if (productInfo) {
        releaseProduct(productInfo).then(res => {
          if (res.code === 200) {
            this.$modal.msgSuccess(res.msg);
            //数据还原
            this.initData();
          } else {
            this.$modal.msgError(res.msg);
          }
        })
      }
    },
    saveProductInfo() {
      let productInfo = this.verifyProductDatas('add');
      if (productInfo) {
        releaseProduct(productInfo).then(res => {
          if (res.code === 200) {
            this.$modal.msgSuccess("已保存");
            this.initData();
          } else {
            this.$modal.msgError(res.msg);
          }
        })
      }
    },
    emptyProductInfo() {
      //数据还原
      this.initData();
    },
    profitMarginHandle(data) {
      if (data.price && data.price !== '' && this.purchasingCost && this.purchasingCost !== '') {
        //获取海外运费
        let gear = this.mercado_gears.filter(item => item.value === this.gears)[0];
        let gearNew = JSON.parse(gear.raw.remark);
        //得到的美元运费
        let s = gearNew[data.value];
        s = ((Number(s) * Number(this.rate)));
        //销售价格
        let priceNew = ((Number(data.price) * Number(this.rate)));
        //成本this.purchasingCost
        //总利润
        let coe = '0.8';
        this.dict.type.mercado_comm.forEach(item => {
          if (item.value === data.value) {
            coe = item.label;
          }
        });
        let ofr = ((priceNew * Number(coe)) - s - this.purchasingCost);
        return (((ofr / priceNew) * 0.99 * 100).toFixed(2)) + '%';
      } else {
        return ''
      }

    },
    profitHandle(data) {
      if (data.price && data.price !== '' && this.purchasingCost && this.purchasingCost !== '') {
        //获取海外运费
        let gear = this.mercado_gears.filter(item => item.value === this.gears)[0];
        let gearNew = JSON.parse(gear.raw.remark);
        //得到的美元运费
        let s = gearNew[data.value];
        s = ((Number(s) * Number(this.rate)));
        //销售价格
        let priceNew = ((Number(data.price) * Number(this.rate)));
        //成本this.purchasingCost
        //总利润
        let coe = '0.8';
        this.dict.type.mercado_comm.forEach(item => {
          if (item.value === data.value) {
            coe = item.label;
          }
        });
        return (((priceNew * Number(coe)) - s - this.purchasingCost) * 0.99).toFixed(2);
      } else {
        return ''
      }
    },
    getUrlInfo() {
      //this.getUrlInfoShow = true;
      if (!this.mercadoUrl || this.mercadoUrl.trim() === '') {
        this.$modal.msgError("美客多产品连接不能为空");
      } else {
        this.loading = true;
        getMercadoUrlInfo({ mercadoInfo: this.mercadoUrl }).then(res => {
          if (res.code === 200) {
            let data = res.data;
            this.sitesPrice.forEach(item => {
              data.sitesPrice.forEach(item2 => {
                if (item.value === item2.id) {
                  item.price = item2.price;
                }
              })
            });
            this.englishTitle = data.englishTitle;
            this.spanishTitle = data.spanishTitle;
            this.portugueseTitle = data.portugueseTitle;
            this.infoList = data.infoList;
            this.colorTags = data.colorMap.colorTags;
            this.sizeTags = data.sizeMap.sizeTags;
            //this.gears = data.gears;
            this.colorTags.forEach(item1 => {
              if (this.sizeTags.length === 0) {
                this.skuList.push('DEF' + '_' + item1);
              }
              this.sizeTags.forEach(item2 => {
                this.skuList.push('DEF' + '_' + item1 + '_' + item2);
              })
            });

            this.colorId = data.colorMap.id;
            this.sizeId = data.sizeMap.id;
            this.cpProdutId = data.id;
            this.price = data.price;
            this.categoryId = data.categoryId;
            if (this.colorTags.length > 0) {
              this.variant = true
            } else {
              this.variant = false
            }
            this.colorImagesList = data.colorImagesList;
            this.fileList = data.colorImagesList;
            this.spanishMsg = data.spanishMsg;
            this.portugueseMsg = data.portugueseMsg;
            //去查询尺码
            if (data.domainId != null) {
              this.sizeMecdoType = data.domainId;
            }
            if (data.genderId != null) {
              this.sizeMecdoGn = data.genderId;
            }
            //this.sizeMecdoId = null;
            //let shopinfo = this.shops.filter(shop => this.shopIds[0] === shop.id)[0];
            //this.getSizeChartVue(this.sizeMecdoType,this.sizeMecdoGn,shopinfo.merchantId);
          } else {
            this.$modal.msgError(res.msg);
          }
          this.loading = false;
        }).catch((e) => {
          //this.$modal.msgError("哎呀出错了,请联系管理员");
          //this.loading = false;
        })
      }
    },
    getSizeChartVue(domainId, genderId, merchantId) {
      getSizeChart({ merchantId, domainId, genderId }).then(res => {
        if (res.code === 200) {
          this.sizes = res.data;
        }
      })
    },
    returnTo() {
      this.$router.push('/mercado/product')
    },
    initData() {
      this.empty();
      this.mercado_sites = this.dict.type.mercado_sites;
      //this.siteIds = [this.mercado_sites[0].dictValue];
      this.mercado_gears = this.dict.type.mercado_gears;
      //this.gears = this.mercado_gears[4].dictValue;
      getRateInfo({ "fromSate": "USD", "toSate": "CNY" }).then(res => {
        if (res.data.code === 1) {
          this.rate = res.data.data.price;
        }
      });

      listShop({ pageNum: 1, pageSize: 100, deptId: this.$store.state.user.deptId }).then(response => {
        this.shops = response.rows;
        //this.shopIds.push(this.shops[0].id);
        // this.shopIds = [this.shops[0].id];
        if (this.$route.query.type === 'query') this.shopIds = [this.shops[0].id];
      });

      //判断是新产品采集还是产品查看还是产品编辑
      //获取路由参数
      const query = this.$route.query;
      //说明是查看产品信息

      if (query.id && query.type === 'query') {
        this.disableType = true;
        this.getProductInfoById(query.id);
        //说明是编辑产品信息
      } else if (query.id && query.type === 'edit') {
        this.getProductInfoById(query.id);
      }
    },
    empty() {
      this.colorImagesList = [];
      this.colorTags = [];
      this.sizeTags = [];
      this.verInfos = [];
      this.infoList = [];
      this.fileList = [];
      this.dialogVisible = false;
      this.extImagesUrl = null;
      this.skuPre = '';
      this.skuPreOld = null;
      this.englishTitle = null;
      this.spanishTitle = null;
      this.portugueseTitle = null;
      this.spanishMsg = null;
      this.portugueseMsg = null;
      this.shopIds = [];
      this.siteIds = [];
      this.shops = [];
      this.sitesPrice = [];
      this.purchasingCost = undefined;
      this.gears = null;
      this.mercado_sites = [];
      this.mercado_gears = [];
      this.rate = '6.8';
      this.mercadoUrl = null;
      this.getUrlInfoShow = true;
      this.loading = false;
      this.variant = false;
      this.colorId = null;
      this.sizeId = null;
      this.productInfo = [];
      this.skuList = [];
      this.cpProdutId = null;
      this.price = undefined;
      this.categoryId = null;
      this.disableType = false;
      this.productId = null;
    },
    getProductInfoById(id) {
      this.loading = true;
      getProductInfoById(id).then(res => {
        if (res.code === 200) {
          let data = res.data;
          this.siteIds = data.sitesPrice.map(item => item.id);
          this.sitesPrice.forEach(item => {
            data.sitesPrice.forEach(item2 => {
              if (item.value === item2.id) {
                item.price = item2.price;
              }
            })
          });
          this.englishTitle = data.englishTitle;
          this.spanishTitle = data.spanishTitle;
          this.portugueseTitle = data.portugueseTitle;
          this.infoList = data.infoList;
          this.mercadoUrl = data.mercadoUrl;
          this.productId = data.productId;
          this.skuPre = data.skuPre;
          this.gears = data.gears;
          this.sizeMecdoType = data.domainId
          this.sizeMecdoGn = data.genderId

          this.colorTags = data.colorMap.colorTags;
          this.sizeTags = data.sizeMap.sizeTags;

          this.colorTags.forEach(item1 => {
            if (this.sizeTags.length === 0) {
              this.skuList.push('DEF' + '_' + item1);
            }
            this.sizeTags.forEach(item2 => {
              this.skuList.push('DEF' + '_' + item1 + '_' + item2);
            })
          });

          this.colorId = data.colorMap.id;
          this.sizeId = data.sizeMap.id;
          this.cpProdutId = data.id;
          this.price = data.price;
          this.categoryId = data.categoryId;
          if (this.colorTags.length > 0) {
            this.variant = true
          } else {
            this.variant = false
          }
          this.colorImagesList = data.colorImagesList;
          this.fileList = data.colorImagesList;
          this.spanishMsg = data.spanishMsg;
          this.portugueseMsg = data.portugueseMsg;
          this.shopIds = data.shopIds

        } else {
          this.$modal.msgError(res.msg);
        }
        this.loading = false;
      })
    },
    deleteImages() {
      //批量删除颜色图片

      this.colorImagesList.forEach(item => {
        item.fileList.length = 0;
      });
      let [...newArr] = this.colorImagesList;
      this.colorImagesList = newArr;

    }
  }
};
</script>
