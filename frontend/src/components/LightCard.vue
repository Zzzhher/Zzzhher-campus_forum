<template>
  <div class="light-card">
    <slot/>
  </div>
</template>

<style scoped>
.light-card {
  background-color: var(--el-bg-color);
  border-radius: 5px;
  padding: 10px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
  margin-bottom: 10px;
}

.light-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}
</style>