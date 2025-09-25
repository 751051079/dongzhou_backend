import request from '@/utils/request'

export function listOrderInfo(query) {
  return request({
    url: '/order/info/list',
    post: 'get',
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

// 删除信息
export function deleteItem(ids) {
  return request({
    url: `/order/info/${ids}`,
    method: 'delete'
  })
}

// 更新链接
export function updateShopOrder(id) {
  return request({
    url: `/order/info/syncShopOrderInfo/${id}`,
    method: 'get'
  })
}

// 批量更新
export function batchUpdate(id) {
  return request({
    url: `/order/info/updateOrderInfo/${id}`,
    method: 'get'
  })
}

// 采集链接
export function collectLink(data) {
  return request({
    url: '/order/shop/item/batch/collection/toShop',
    method: 'post',
    data
  })
}