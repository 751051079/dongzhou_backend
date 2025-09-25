import request from '@/utils/request'

// 查询店铺
export function listShop(query) {
  return request({
    url: '/system/shop/list',
    method: 'get',
    params: query
  })
}
// 查询店铺站点
export function listShopInfoSite(query) {
  return request({
    url: '/system/shop/site/list',
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
export function deleteShop(id) {
  return request({
    url: `/system/shop/${id}`,
    method: 'delete',
  })
}

//修改价格系数
export function editPriceRatio(data) {
  return request({
    url: '/system/shop/editPriceRatio',
    method: 'post',
    data
  })
}



