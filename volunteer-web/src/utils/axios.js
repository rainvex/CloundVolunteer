import axios from 'axios';
import { ElMessage } from 'element-plus';
import store from '../store/index';
import router from '../router/index';

const myAxios = axios.create({
    timeout: 5000,
    baseURL: 'http://localhost:8081'
})

//添加请求拦截器
myAxios.interceptors.request.use(config => {
        // 在发送请求前的操作
        // 判断是否存在token,如果存在将每个请求的header添加token
        if (store.state.token) {
            config.headers.common.Authorization = "Bearer " + store.state.token;
        }
        return config;
    },
    error => {
        console.log(error);
        return Promise.reject(error);
    }
);

// 添加响应拦截器
myAxios.interceptors.response.use(response => {
        if (response.data.code === 401) {
            router.push('/login');
            store.commit('setToken','');
            ElMessage.error('请先登录或登录失效需重新登录');
        }
        return response;
    }, error => {
        return Promise.reject(error);
});

export default myAxios;