import axios from 'axios';
import { accessHeader } from '@/net';

const activityApi = {
  // 获取活动列表
  getActivityList: async () => {
    try {
      const response = await axios.get('/api/activity/list', { headers: accessHeader() });
      return response.data.data || [];
    } catch (error) {
      console.error('获取活动列表失败:', error);
      return [];
    }
  },
  // 获取活动详情
  getActivityDetail: async (id) => {
    try {
      const response = await axios.get(`/api/activity/detail/${id}`, { headers: accessHeader() });
      return response.data.data;
    } catch (error) {
      console.error('获取活动详情失败:', error);
      return null;
    }
  },
  // 获取热门活动
  getPopularActivities: async (limit = 5) => {
    try {
      const response = await axios.get(`/api/activity/popular?limit=${limit}`, { headers: accessHeader() });
      return response.data.data || [];
    } catch (error) {
      console.error('获取热门活动失败:', error);
      return [];
    }
  },
  // 创建活动
  createActivity: async (data) => {
    try {
      const response = await axios.post('/api/activity/create', data, { headers: accessHeader() });
      return response.data;
    } catch (error) {
      console.error('创建活动失败:', error);
      return { code: 400, message: '活动发布失败' };
    }
  },
  // 报名活动
  registerActivity: async (id) => {
    try {
      const response = await axios.post(`/api/activity/register/${id}`, {}, { headers: accessHeader() });
      return response.data;
    } catch (error) {
      console.error('报名活动失败:', error);
      return { code: 400, message: '报名失败' };
    }
  },
  // 签到活动
  checkInActivity: async (id) => {
    try {
      const response = await axios.post(`/api/activity/checkin/${id}`, {}, { headers: accessHeader() });
      return response.data;
    } catch (error) {
      console.error('签到活动失败:', error);
      return { code: 400, message: '签到失败' };
    }
  },
  // 获取活动参与者列表
  getActivityParticipants: async (id) => {
    try {
      const response = await axios.get(`/api/activity/participants/${id}`, { headers: accessHeader() });
      return response.data.data || [];
    } catch (error) {
      console.error('获取活动参与者列表失败:', error);
      return [];
    }
  },
  // 添加活动反馈
  addFeedback: async (id, data) => {
    try {
      const response = await axios.post(`/api/activity/feedback/${id}`, data, { headers: accessHeader() });
      return response.data;
    } catch (error) {
      console.error('添加活动反馈失败:', error);
      return { code: 400, message: '反馈失败' };
    }
  },
  // 获取活动反馈列表
  getActivityFeedbacks: async (id) => {
    try {
      const response = await axios.get(`/api/activity/feedbacks/${id}`, { headers: accessHeader() });
      return response.data.data || [];
    } catch (error) {
      console.error('获取活动反馈列表失败:', error);
      return [];
    }
  },
  // 获取活动状态
  getActivityStatus: async (id) => {
    try {
      const response = await axios.get(`/api/activity/status/${id}`, { headers: accessHeader() });
      return response.data.data;
    } catch (error) {
      console.error('获取活动状态失败:', error);
      return null;
    }
  },
  // 获取签到二维码
  getCheckInQrCode: async (id) => {
    try {
      const response = await axios.get(`/api/activity/checkin-qr/${id}`, { headers: accessHeader() });
      return response.data.data;
    } catch (error) {
      console.error('获取签到二维码失败:', error);
      return null;
    }
  },
};

export default activityApi