module.exports = {
  presets: [
    '@vue/cli-plugin-babel/preset'
  ],
  plugins: [
    '@babel/plugin-transform-private-methods', // 处理私有方法
    '@babel/plugin-transform-class-properties' // 处理类属性
  ]
}
