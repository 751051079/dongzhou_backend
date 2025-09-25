import request from '@/utils/request'

// 查询用户列表
export function listUpcManage(query) {
  return request({
    url: '/system/upc/list',
    method: 'get',
    params: query
  })
}
export function listUpc(query) {
  return request({
    url: '/system/upc/item/list',
    method: 'get',
    params: query
  })
}
export function addUpc(data) {
  return request({
    url: '/system/upc',
    method: 'post',
    data: data
  })
}
export function editUpcInfo(data) {
  return request({
    url: '/system/upc',
    method: 'put',
    data: data
  })
}
export function removeUpcInfo(id) {
  return request({
    url: `/system/upc/${id}`,
    method: 'delete',
  })
}
