<template>
  <el-container class="register-option-container">
    <p class="register-option-link"><span><router-link to="/">返回首页</router-link></span>|<span><router-link to="/login">已有帐号？前往登录</router-link></span></p>
    <el-main class="register-option-main">
      <h3>请选择你的注册角色</h3>
      <p style="font-size: 15px;margin-top: 10px;">欢迎你的注册，让我们共同志愿，共同构建美好幸福生活！</p>
      <div style="display: flex;padding: 60px;">
        <div class="register-option">
          <p>我想成为志愿者</p>
          <div style="padding: 10px">
            <router-link :to="'/register/0'">
              <div id="register-option-volunteer" style="border-color: #ff457a">
                <img src="../../assets/volunteer.png" alt=""/>
                <span>参与志愿活动</span>
              </div>
            </router-link>
          </div>
        </div>
        <div class="register-option">
          <p>我想注册志愿者团队</p>
          <div style="padding: 10px">
            <router-link :to="'/register/1'">
              <div id="register-option-team" style="border-color: #ed5151">
                <img src="../../assets/team.png" alt=""/>
                <span>聚集志愿者</span>
              </div>
            </router-link>
          </div>
        </div>
        <div class="register-option">
          <p>我想发布志愿活动</p>
          <div style="padding: 10px">
            <router-link :to="'/register/2'">
              <div id="register-option-unit" style="border-color: #47e289">
                <img src="../../assets/unit.png" alt=""/>
                <span>发布志愿活动</span>
              </div>
            </router-link>
          </div>
        </div>
      </div>
    </el-main>
  </el-container>
</template>

<script setup>

</script>

<style scoped>
.register-option-container {
  width: 1120px;
  height: auto;
  margin: 30px auto;
  display: flex;
  flex-direction: column;
}
.register-option-link {
  width: 100%;
  text-align: right;
  color: #959393;
}
.register-option-link span {
  margin: 0 5px;
}
.register-option-link a {
  color: #959393;
  font-size: 13px;
  text-decoration: none;
}
.register-option-link a:hover {
  color: #1989fa;
}
.register-option-main {
  width: 100%;
  height: 600px;
  margin-top: 10px;
  background-color: #ffffff;
}
.register-option {
  width: 240px;
  height: 270px;
  margin-left: 60px;
}
.register-option a {
  text-decoration: none;
}
.register-option a div {
  height: 210px;
  border-radius: 50%;
  border: 3px solid;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  margin-top: 20px;
}
.register-option a div img {
  width: 130px;
  height: 110px;
}
.register-option a div span {
  font-size: 13px;
  margin-top: 20px;
}
#register-option-volunteer:hover {
  background-color: #ff457a;
}
#register-option-team:hover {
  background-color: #ed5151;
}
#register-option-unit:hover {
  background-color: #47e289;
}
</style>