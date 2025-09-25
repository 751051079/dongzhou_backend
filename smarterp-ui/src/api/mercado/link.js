import request from '@/utils/request'

// 查询店铺
export function listShop(query) {
  return request({
    url: '/order/link/list',
    method: 'get',
    params: query
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
//绑定店铺
export function addShop(data) {
  return request({
    url: '/system/shop',
    method: 'post',
    data
  })
}
//暂停店铺
export function deleteShop(ids) {
  return request({
    url: `/order/link/${ids}`,
    method: 'delete',
  })
}
// 采集链接
export function addLink(data) {
  return request({
    url: '/order/link/batch/collection/toShop',
    method: 'post',
    data
  })
}
// 爬取链接
export function crawling(data) {
  return request({
    url: '/order/link/batch/collection',
    method: 'post',
    data
  })
}
// 新增链接
export function addLink2(data) {
  return request({
    url: '/order/link/add',
    method: 'post',
    data
  })
}