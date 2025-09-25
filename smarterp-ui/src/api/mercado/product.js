import request from '@/utils/request'

export function uploadMercadoApi(data) {
  return request({
    url: '/order/product/uploadMercadoApi',
    method: 'post',
    data
  })
}
export function uploadImgByUrl(data) {
  return request({
    url: '/order/product/uploadImgByUrl',
    method: 'post',
    data
  })
}
export function getMercadoUrlInfo(query) {
  return request({
    url: '/order/product/getMercadoInfo',
    method: 'post',
    params: query
  })
}
//发布产品
export function releaseProduct(data) {
  return request({
    url: '/order/item/release/productHandleData',
    method: 'post',
    data
  })
}
export function getProductList(query) {
  return request({
    url: '/order/product/list',
    method: 'get',
    params: query
  })
}
export function sendProductOrSiteOrMsg(data) {
  return request({
    url: '/order/product/release',
    method: 'post',
    data
  })
}
export function getProductInfoById(id) {
  return request({
    url: `/order/product/getProductInfoById/${id}`,
    method: 'get'
  })
}
export function editProductInfoById(data) {
  return request({
    url: '/order/product',
    method: 'put',
    data
  })
}
export function deleteProductInfoById(id) {
  return request({
    url: `/order/product/${id}`,
    method: 'delete'
  })
}
//查询尺码
export function getSizeChart(data) {
  return request({
    url: `/order/product/getSizeChart`,
    method: 'post',
    data
  })
}
// 批量删除
export function batchProduct(ids) {
  return request({
    url: `/order/product/batchDetele/${ids}`,
    method: 'delete'
  })
}
// 批量发布
export function bacthSendProduct(ids) {
  return request({
    url: `/order/product/batchRelease/${ids}`,
    method: 'get'
  })
}
// 错误日志
export function getErrorLog(id) {
  return request({
    url: `/order/product/getErrorLog/${id}`,
    method: 'get'
  })
}
// 复制到
export function copyTo(data) {
  return request({
    url: '/order/product/batch/copy/toShop',
    method: 'post',
    data
  })
}
// 查询店铺
export function selectListShop(query) {
  return request({
    url: '/system/shop/list',
    method: 'get',
    params: query
  })
}