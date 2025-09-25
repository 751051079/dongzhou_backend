import request from '@/utils/request'

// 查询用户列表
export function listCombo(query) {
  return request({
    url: '/system/combo/list',
    method: 'get',
    params: query
  })
}
//查询套餐详情
export function getComboInfo(id) {
  return request({
    url: `/system/combo/${id}`,
    method: 'get'
  })
}
//新增套餐
export function addCombo(data) {
  return request({
    url: '/system/combo',
    method: 'post',
    data: data
  })
}
//修改套餐
export function editCombo(data) {
  return request({
    url: '/system/combo',
    method: 'put',
    data: data
  })
}
//删除套餐
export function deleteCombo(id) {
  return request({
    url: `/system/combo/${id}`,
    method: 'delete',
  })
}
//部门绑定套餐
export function deptBingCombo(data) {
  return request({
    url: '/system/deptcombo',
    method: 'post',
    data: data
  })
}
