import { createStore } from 'vuex';
import createPersistedState from 'vuex-persistedstate';
import jwt_decode from 'jwt-decode';

export default createStore({
  state: {
    token: '',
    activeKey: '',
    informKey: '',
    loginUser: {
      userId: '',
      userType: ''
    },
    detail: {
      userKey: '',
      userType: ''
    },
    newMessageCount: 0
  },
  getters: {
  },
  mutations: {
    setToken(state, token){
      state.token = token;
    },
    setActiveKey(state, activeKey) {
      state.activeKey = activeKey;
    },
    setInformKey(state, informKey) {
      state.informKey = informKey;
    },
    setLoginUser(state, token) {
      if (token === '') {
        state.loginUser.userId = '';
        state.loginUser.userType = '';
      } else {
        let user = jwt_decode(token);
        state.loginUser.userId = user.userId;
        state.loginUser.userType = user.userType;
      }
    },
    setDetail(state, detailKey) {
      state.detail.userKey = detailKey.userKey;
      state.detail.userType = detailKey.userType;
    },
    setMessageCount(state, messageCount) {
      state.newMessageCount = messageCount;
    }
  },
  actions: {
  },
  modules: {
  },
  plugins: [
      createPersistedState({storage: window.sessionStorage})
  ]
})
