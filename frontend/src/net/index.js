import axios from "axios";
import {ElMessage} from "element-plus";
import router from "@/router";
import {userStore} from "@/store";

const authItemName = "authorize"

// 缓存对象，用于存储GET请求的结果
const cache = {
    data: {},
    // 默认缓存过期时间：5分钟
    defaultExpire: 5 * 60 * 1000,
    
    // 设置缓存
    set(key, value, expire = this.defaultExpire) {
        this.data[key] = {
            value,
            expire: Date.now() + expire
        };
    },
    
    // 获取缓存
    get(key) {
        const item = this.data[key];
        if (!item) return null;
        
        // 检查缓存是否过期
        if (Date.now() > item.expire) {
            delete this.data[key];
            return null;
        }
        
        return item.value;
    },
    
    // 删除缓存
    delete(key) {
        delete this.data[key];
    },
    
    // 清空所有缓存
    clear() {
        this.data = {};
    },
    
    // 清除指定前缀的缓存
    clearByPrefix(prefix) {
        Object.keys(this.data).forEach(key => {
            if (key.startsWith(prefix)) {
                delete this.data[key];
            }
        });
    }
};

const accessHeader = () => {
    return {
        'Authorization': `Bearer ${takeAccessToken()?.token}`
    }
}

const defaultError = (error) => {
    console.error(error)
    const status = error.response.status
    if (status === 429) {
        ElMessage.error(error.response.data.message)
    } else {
        ElMessage.error('发生了一些错误，请联系管理员')
    }
}

const defaultFailure = (message, status, url) => {
    console.warn(`请求地址: ${url}, 状态码: ${status}, 错误信息: ${message}`)
    ElMessage.warning(message)
}

function takeAccessToken() {
    const str = localStorage.getItem(authItemName) || sessionStorage.getItem(authItemName);
    if(!str) return null
    const authObj = JSON.parse(str)
    if(new Date(authObj.expire) <= new Date()) {
        deleteAccessToken()
        ElMessage.warning("登录状态已过期，请重新登录！")
        return null
    }
    return authObj
}

function storeAccessToken(remember, token, expire, role){
    const authObj = { token, expire, role }
    const str = JSON.stringify(authObj)
    if(remember)
        localStorage.setItem(authItemName, str)
    else
        sessionStorage.setItem(authItemName, str)
}

function deleteAccessToken(redirect = false) {
    localStorage.removeItem(authItemName)
    sessionStorage.removeItem(authItemName)
    // 清除缓存
    cache.clear();
    if(redirect) {
        router.push({ name: 'welcome-login' })
    }
}

function internalPost(url, data, headers, success, failure, error = defaultError){
    axios.post(url, data, { headers: headers }).then(({data}) => {
        if(data.code === 200) {
            success(data.data)
        } else if(data.code === 401) {
            failure(data.message ? data.message : '登录状态已过期，请重新登录！')
            deleteAccessToken(true)
        } else {
            failure(data.message, data.code, url)
        }
    }).catch(err => error(err))
}

function internalGet(url, headers, success, failure, error = defaultError, useCache = true){
    // 生成缓存键
    const cacheKey = url + JSON.stringify(headers || {});
    
    // 检查缓存
    if (useCache) {
        const cachedData = cache.get(cacheKey);
        if (cachedData) {
            console.log(`从缓存获取数据: ${url}`);
            success(cachedData);
            return;
        }
    }
    
    // 发起请求
    axios.get(url, { headers: headers }).then(({data}) => {
        if(data.code === 200) {
            // 缓存结果
            if (useCache) {
                cache.set(cacheKey, data.data);
            }
            success(data.data)
        } else if(data.code === 401) {
            failure('登录状态已过期，请重新登录！')
            deleteAccessToken(true)
        } else {
            failure(data.message, data.code, url)
        }
    }).catch(err => error(err))
}

function login(username, password, remember, success, failure = defaultFailure){
    internalPost('/api/auth/login', {
        username: username,
        password: password
    }, {
        'Content-Type': 'application/x-www-form-urlencoded'
    }, (data) => {
        storeAccessToken(remember, data.token, data.expire, data.role)
        ElMessage.success(`登录成功，欢迎 ${data.username} 来到我们的系统`)
        success(data)
    }, failure)
}

function post(url, data, success, failure = defaultFailure) {
    internalPost(url, data, accessHeader() , (data) => {
        // 清除相关缓存
        const cachePrefix = url.split('/').slice(0, -1).join('/');
        if (cachePrefix) {
            cache.clearByPrefix(cachePrefix);
        }
        success(data);
    }, failure)
}

function fetchPost(url, data) {
    return fetch(axios.defaults.baseURL + url, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${takeAccessToken()?.token}`
        },
        body: JSON.stringify(data)
    })
}

function logout(success, failure = defaultFailure){
    get('/api/auth/logout', () => {
        deleteAccessToken()
        ElMessage.success(`退出登录成功，欢迎您再次使用`)
        success()
    }, failure)
}

function get(url, success, failure = defaultFailure, useCache = true) {
    internalGet(url, accessHeader(), success, failure, defaultError, useCache)
}

function isUnauthorized() {
    return !takeAccessToken()
}
function isRoleAdmin() {
    const tokenRole = takeAccessToken()?.role === 'admin';
    const store = userStore();
    const storeRole = store.user?.role === 'admin';
    return tokenRole || storeRole;
}

export { post, get, login, logout, isUnauthorized, isRoleAdmin, accessHeader, fetchPost, cache }
