<template>
  <div class="component-upload-image">

    <div v-for="(item, index) in colorImagesList" :key="item.color">
      <el-card shadow="hover">
        变体颜色:{{item.color}}
        <el-button type="primary" @click="addExtImagesUrl(item.color)">网络图片</el-button>
        <el-button type="primary" @click="editMainImage(item,colorImagesList)">设置为主图</el-button>
        <el-button type="danger" @click="deleteImages(item,colorImagesList)">删除图片</el-button>
        <el-select v-model="valueColors" clearable multiple placeholder="请选择要复制到的颜色">
          <el-option
            v-for="item2 in colorImagesList"
            :key="item2.color"
            :label="item2.color"
            :value="item2.color">
          </el-option>
        </el-select>
        <el-button type="success" @click="cpImages(item,colorImagesList)">复制图片</el-button>
        <br>
        <br>
        <div style="display: flex;">
          <el-upload
            action="#"
            list-type="picture-card"
            :file-list="item.fileList"
            :data="item"
            :on-change="uploadChange"
            :show-file-list="false"
            accept=".jpg,.png,.jpeg,.JPG,.JPEG,.webp"
            :before-upload="beforeUpload"
            :http-request="uploadImg"
            :auto-upload="true">
            <i slot="default" class="el-icon-plus"></i>
          </el-upload>
          <!-- 使用element-ui自带样式 -->
          <ul class="el-upload-list el-upload-list--picture-card">
            <draggable v-model="item.fileList" @end="endHandle()">
              <li v-for="(item2, index2) in item.fileList" :key="item2.uid" class="el-upload-list__item">
                <img :src="item2.url" alt="" class="el-upload-list__item-thumbnail " >
                <i class="el-icon-close"></i>
                <span class="el-upload-list__item-actions">
                    <!-- 预览 -->
                    <span class="el-upload-list__item-preview" @click="handlePictureCardPreview(item2)">
                      <i class="el-icon-zoom-in"></i>
                    </span>
                  <!-- 删除 -->
                    <span class="el-upload-list__item-delete" @click="handleRemove(index2,item.fileList)">
                      <i class="el-icon-delete"></i>
                    </span>
                  </span>
              </li>
            </draggable>
          </ul>
        </div>
      </el-card>
    </div>

    <el-dialog :visible.sync="dialogVisible">
      <el-image :src="dialogImageUrl"></el-image>
    </el-dialog>

    <el-dialog :visible.sync="dialogVisibleIn" v-loading="loading">
      <el-input
        type="textarea"
        :rows="2"
        placeholder="请输入图片地址/多个地址换行填入(一行一个地址)"
        v-model="extImagesUrl">
      </el-input>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="extImagesUrlSubmit">确 定</el-button>
        <el-button @click="extImagesUrlCancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
  import {uploadMercadoApi, uploadImgByUrl} from "@/api/mercado/product";
  import draggable from 'vuedraggable'

  export default {
    name: "MercadoImage",
    components: {draggable},
    props: {
      colorImagesList: {
        type: Array,
        default: () => [],
      }
    },
    data() {
      return {
        dialogImageUrl: '',
        dialogVisible: false,
        disabled: false,
        fileType: ['jpg', 'png', 'jpeg', 'JPG', 'JPEG', 'webp'],
        dialogVisibleIn: false,
        extImagesUrl: null,
        addExtImagesColor: null,
        loading: false,
        valueColors: []
      };
    },
    created() {
      //console.log(this.colorImagesList);
    },
    watch: {},
    methods: {
      handleRemove(index, fileList) {
        fileList.splice(index, 1);
        this.$emit('changeFileListEvent', this.colorImagesList);
      },
      handlePictureCardPreview(file) {
        //console.log(file);
        this.dialogImageUrl = file.url;
        this.dialogVisible = true;
      },
      uploadChange(file, fileList) {
        //   console.log(file);
        //   this.fileList.push(file);
      },
      uploadImg(f) {
        let file = f.file;
        let formData = new FormData();
        formData.append('file', file);
        uploadMercadoApi(formData).then(response => {
          if (response.code === 200) {
            //上传成功
            // this.fileList.forEach(item => {
            //   if (file.uid === item.uid) {
            //     item.uuid = response.data;
            //   }
            // })
            //上传成功返回图片id和url
            this.colorImagesList.forEach(item => {
              if (item.color === f.data.color) {
                item.fileList.push({id: response.data.id, url: response.data.url});
              }
            });
            this.$emit('changeFileListEvent', this.colorImagesList);
          } else {
            this.$modal.msgError(response.msg);
            //需要文件列表去除文件
            //this.fileList = this.fileList.filter(item => item.uid !== file.uid);
          }
        }).catch(() => {
          //需要文件列表去除文件
          //this.fileList = this.fileList.filter(item => item.uid !== file.uid);
        });
      },
      beforeUpload(file) {
        // 校检文件类型
        if (this.fileType) {
          const fileName = file.name.split('.');
          const fileExt = fileName[fileName.length - 1];
          const isTypeOk = this.fileType.indexOf(fileExt) >= 0;
          if (!isTypeOk) {
            this.$modal.msgError(`文件格式不正确, 请上传${this.fileType.join("/")}格式文件!`);
            //this.fileList = this.fileList.filter(item => item.uid !== file.uid);
            return false;
          }
        }
      },
      extImagesUrlSubmit() {
        this.loading = true;
        uploadImgByUrl({urls: this.extImagesUrl}).then(response => {
          if (response.code === 200) {
            let data = response.data;
            if (data.length > 0) {
              this.colorImagesList.forEach(item => {
                if (item.color === this.addExtImagesColor) {
                  item.fileList = item.fileList.concat(data);
                }
              });
              this.$emit('changeFileListEvent', this.colorImagesList);
            }
          }
        }).finally(() => {
          this.addExtImagesColor = null;
          this.extImagesUrl = null;
          this.dialogVisibleIn = false;
          this.loading = false;
        })
      },
      extImagesUrlCancel() {
        this.addExtImagesColor = null;
        this.extImagesUrl = null;
        this.dialogVisibleIn = false;
      },
      addExtImagesUrl(color) {
        this.addExtImagesColor = color;
        this.dialogVisibleIn = true;
      },
      endHandle() {
        this.$emit('changeFileListEvent', this.colorImagesList);
      },
      editMainImage(item, data) {
        data.forEach((item1, index) => {
          if (item1.color === item.color) {
            data.splice(index, 1);
          }
        });
        data.unshift(item);
        this.$emit('changeFileListEvent', data);
      },
      deleteImages(item, data) {
        //批量删除颜色图片
        item.fileList = [];
        this.$emit('changeFileListEvent', data);
      },
      cpImages(item, data){
        data.forEach(item2 =>{
          this.valueColors.forEach(item3 =>{
            if (item2.color === item3 && item3 !== item.color){
              item2.fileList = [...item2.fileList,...item.fileList];
            }
          });
        });
        this.valueColors = [];
      }
    }
  };
</script>
