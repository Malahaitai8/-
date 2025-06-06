import {defineStore} from 'pinia';
import request from '@/utils/request.js'; // 您的请求工具

const ORG_SESSION_KEY = 'xm-pro-organization';

export const useOrganizationStore = defineStore('organization', {
    state: () => ({
        loggedInOrganizationBasicInfo: JSON.parse(localStorage.getItem(ORG_SESSION_KEY)) || null, // 存储登录用户的基本信息，如 orgLoginUserName
        // 存储通过 API 获取的详细组织信息 (严格对应 Organization.java 实体属性名)
        detailedOrganizationInfo: {
            orgId: '',
            orgName: '',
            orgLoginUserName: '',
            // orgLoginPassword: '', // 通常不在前端状态中存储密码
            contactPersonPhone: '',
            serviceRegion: '',
            orgScale: 0,
            orgRating: 0.0,
            orgAccountStatus: '',
            totalServiceHours: 0,
            activityCount: 0,
            trainingCount: 0
        },
        isLoading: false,
        error: null,
    }),

    getters: {
        displayName: (state) => state.detailedOrganizationInfo.orgName || state.detailedOrganizationInfo.orgLoginUserName || state.loggedInOrganizationBasicInfo?.orgLoginUserName || '组织用户',
        isAuthenticated: (state) => !!state.loggedInOrganizationBasicInfo,
        currentOrganizationId: (state) => state.detailedOrganizationInfo.orgId,
        rating: (state) => state.detailedOrganizationInfo.orgRating,
        // 其他需要的 getters
    },

    actions: {
        loginSuccess(apiResponseData, basicLoginInfo) {
            // apiResponseData 是后端 /organization/login 返回的 res.data (包含完整的组织信息，密码已擦除)
            // basicLoginInfo 包含 { orgLoginUserName: 'xxx' }
            this.loggedInOrganizationBasicInfo = basicLoginInfo;
            localStorage.setItem(ORG_SESSION_KEY, JSON.stringify(basicLoginInfo));

            if (apiResponseData && apiResponseData.orgLoginUserName) {
                // 使用后端返回的完整数据更新 detailedOrganizationInfo
                this.detailedOrganizationInfo.orgId = apiResponseData.orgId || '';
                this.detailedOrganizationInfo.orgName = apiResponseData.orgName || '';
                this.detailedOrganizationInfo.orgLoginUserName = apiResponseData.orgLoginUserName || basicLoginInfo.orgLoginUserName;
                // this.detailedOrganizationInfo.orgLoginPassword = ''; // 密码不存储
                this.detailedOrganizationInfo.contactPersonPhone = apiResponseData.contactPersonPhone || '';
                this.detailedOrganizationInfo.serviceRegion = apiResponseData.serviceRegion || '';
                this.detailedOrganizationInfo.orgScale = Number(apiResponseData.orgScale) || 0;
                this.detailedOrganizationInfo.orgRating = Number(apiResponseData.orgRating) || 0.0;
                this.detailedOrganizationInfo.orgAccountStatus = apiResponseData.orgAccountStatus || '';
                this.detailedOrganizationInfo.totalServiceHours = Number(apiResponseData.totalServiceHours) || 0;
                this.detailedOrganizationInfo.activityCount = Number(apiResponseData.activityCount) || 0;
                this.detailedOrganizationInfo.trainingCount = Number(apiResponseData.trainingCount) || 0;
                console.log('Store (Organization): detailedOrganizationInfo 在 loginSuccess 后更新:', JSON.parse(JSON.stringify(this.detailedOrganizationInfo)));
            } else {
                // 如果登录接口不返回详细信息或数据不完整，尝试重新获取
                this.fetchDetailedOrganizationInfo();
            }
        },

        clearDetailedOrganizationInfo() {
            this.detailedOrganizationInfo = {
                orgId: '',
                orgName: '',
                orgLoginUserName: '',
                contactPersonPhone: '',
                serviceRegion: '',
                orgScale: 0,
                orgRating: 0.0,
                orgAccountStatus: '',
                totalServiceHours: 0,
                activityCount: 0,
                trainingCount: 0,
            };
            console.log('Store (Organization): detailedOrganizationInfo cleared.');
        },

        async fetchDetailedOrganizationInfo() {
            if (!this.loggedInOrganizationBasicInfo || !this.loggedInOrganizationBasicInfo.orgLoginUserName) {
                console.warn('Store (Organization): No logged-in organization (orgLoginUserName) to fetch details for.');
                this.clearDetailedOrganizationInfo();
                return;
            }
            this.isLoading = true;
            this.error = null;
            try {
                console.log(`Store (Organization): Fetching detailed info for orgLoginUserName: ${this.loggedInOrganizationBasicInfo.orgLoginUserName}`);
                const res = await request.get('/organization/selectByOrgLoginUserName', {
                    params: { orgLoginUserName: this.loggedInOrganizationBasicInfo.orgLoginUserName.trim() }
                });
                console.log('Store (Organization): API response for detailed info:', res);
                if (res.code === '200' && res.data) {
                    this.detailedOrganizationInfo.orgId = res.data.orgId || '';
                    this.detailedOrganizationInfo.orgName = res.data.orgName || '';
                    this.detailedOrganizationInfo.orgLoginUserName = res.data.orgLoginUserName || this.loggedInOrganizationBasicInfo.orgLoginUserName;
                    this.detailedOrganizationInfo.contactPersonPhone = res.data.contactPersonPhone || '';
                    this.detailedOrganizationInfo.serviceRegion = res.data.serviceRegion || '';
                    this.detailedOrganizationInfo.orgScale = Number(res.data.orgScale) || 0;
                    this.detailedOrganizationInfo.orgRating = Number(res.data.orgRating) || 0.0;
                    this.detailedOrganizationInfo.orgAccountStatus = res.data.orgAccountStatus || '';
                    this.detailedOrganizationInfo.totalServiceHours = Number(res.data.totalServiceHours) || 0;
                    this.detailedOrganizationInfo.activityCount = Number(res.data.activityCount) || 0;
                    this.detailedOrganizationInfo.trainingCount = Number(res.data.trainingCount) || 0;
                    console.log('Store (Organization): detailedOrganizationInfo updated:', JSON.parse(JSON.stringify(this.detailedOrganizationInfo)));
                } else {
                    console.error('Store (Organization): Failed to fetch detailed organization info (API Error or no data):', res.msg);
                    this.error = res.msg || '获取组织详细信息失败';
                    this.clearDetailedOrganizationInfo();
                }
            } catch (err) {
                console.error('Store (Organization): Exception during API call for detailed organization info:', err);
                this.error = '请求组织详细信息接口出错';
                this.clearDetailedOrganizationInfo();
            } finally {
                this.isLoading = false;
            }
        },

        async updateOrganizationInfo(organizationData) {
            // organizationData 的字段名应已是驼峰式并与后端实体对应
            if (!organizationData || !organizationData.orgId) { //
                this.error = '更新组织信息失败：组织ID缺失'; //
                console.error('Store (Organization): orgId is required for update.'); //
                return {code: '400', msg: '组织ID是必须的'}; //
            }
            this.isLoading = true; //
            this.error = null; //
            try {
                console.log('Store (Organization): Updating organization info:', organizationData); //
                const payload = { //
                    ...organizationData, //
                    orgScale: Number(organizationData.orgScale) || 0, //
                    orgRating: Number(organizationData.orgRating) || 0.0, //
                    totalServiceHours: Number(organizationData.totalServiceHours) || 0, //
                    activityCount: Number(organizationData.activityCount) || 0, //
                    trainingCount: Number(organizationData.trainingCount) || 0, //
                };
                const res = await request.put('/organization/updateByOrgId', payload); //
                if (res.code === '200') { //
                    console.log('Store (Organization): Organization info updated successfully.'); //
                    await this.fetchDetailedOrganizationInfo(); //
                } else {
                    this.error = res.msg || '更新组织信息失败'; //
                    console.error('Store (Organization): Failed to update organization info:', res.msg); //
                }
                return res; //
            } catch (err) {
                this.error = '更新组织信息时发生错误'; //
                console.error('Store (Organization): Error updating organization info:', err); //
                throw err; //
            } finally {
                this.isLoading = false; //
            }
        },

        async changeOrganizationPassword({ orgId, oldPassword, newPassword }) {
            if (!orgId || !oldPassword || !newPassword) {
                this.error = '修改密码失败：参数不完整';
                return {code: '400', msg: '组织ID、旧密码和新密码均不能为空'};
            }
            this.isLoading = true;
            this.error = null;
            try {
                console.log(`Store (Organization): Changing password for organization ID: ${orgId}`);
                const res = await request.post('/organization/changePassword', {
                    orgId, // 后端Controller中payload.get("orgId")，所以这里保持
                    oldPassword,
                    newPassword
                });
                if (res.code === '200') {
                    console.log('Store (Organization): Password changed successfully.');
                } else {
                    this.error = res.msg || '修改密码失败';
                    console.error('Store (Organization): Failed to change password:', res.msg);
                }
                return res;
            } catch (err) {
                this.error = '修改密码时发生错误';
                console.error('Store (Organization): Error changing password:', err);
                throw err;
            } finally {
                this.isLoading = false;
            }
        },

        logout() {
            this.loggedInOrganizationBasicInfo = null;
            this.clearDetailedOrganizationInfo();
            localStorage.removeItem(ORG_SESSION_KEY);
            console.log('Store (Organization): Logged out, session cleared.');
        },

        initializeStore() {
            const storedOrgUser = localStorage.getItem(ORG_SESSION_KEY);
            if (storedOrgUser) {
                try {
                    this.loggedInOrganizationBasicInfo = JSON.parse(storedOrgUser);
                    if (this.loggedInOrganizationBasicInfo && this.loggedInOrganizationBasicInfo.orgLoginUserName) {
                        this.fetchDetailedOrganizationInfo();
                    } else {
                        this.logout(); // 如果解析出的基本信息不完整，则登出
                    }
                } catch (e) {
                    console.error("Store (Organization) 初始化时解析 localStorage 用户数据错误:", e);
                    this.logout();
                }
            }
        }
    }
});
