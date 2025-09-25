import request from '@/utils/request'

// 查询用户列表
export function getRateInfo(query) {
  return request({
    url: '/system/ext/getRateInfo',
    method: 'get',
    params: query
  })
}
