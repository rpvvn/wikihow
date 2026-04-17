<template>
  <AdminLayout>
    <div class="user-manage">
      <!-- 搜索和筛选 -->
      <div class="toolbar">
        <div class="search-box">
          <el-input v-model="searchKeyword" placeholder="搜索用户名/邮箱" clearable @keyup.enter="handleSearch">
            <template #prefix><el-icon><Search /></el-icon></template>
          </el-input>
          <el-select v-model="filterRole" placeholder="角色筛选" clearable style="width: 120px">
            <el-option label="全部" value="" />
            <el-option label="管理员" value="ADMIN" />
            <el-option label="审核员" value="REVIEWER" />
            <el-option label="普通用户" value="USER" />
          </el-select>
          <el-select v-model="filterStatus" placeholder="状态筛选" clearable style="width: 120px">
            <el-option label="全部" value="" />
            <el-option label="正常" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button type="success" @click="openAddDialog">
            <el-icon><Plus /></el-icon> 添加用户
          </el-button>
        </div>
      </div>
      
      <!-- 用户列表 -->
      <el-table :data="users" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="用户" min-width="200">
          <template #default="{ row }">
            <div class="user-cell">
              <el-avatar :size="40" :src="row.avatar">{{ row.nickname?.charAt(0) }}</el-avatar>
              <div class="user-info">
                <span class="nickname">{{ row.nickname }}</span>
                <span class="username">@{{ row.username }}</span>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="email" label="邮箱" width="200" />
        <el-table-column label="角色" width="100">
          <template #default="{ row }">
            <el-tag :type="getRoleTagType(row.role)" size="small">
              {{ getRoleText(row.role) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="注册时间" width="180">
          <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="openEditDialog(row)">编辑</el-button>
            <el-button type="warning" link @click="openPasswordDialog(row)">密码</el-button>
            <el-button :type="row.status === 1 ? 'danger' : 'success'" link @click="toggleStatus(row)">
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-button type="info" link @click="openRoleDialog(row)">权限</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination">
        <el-pagination v-model:current-page="page" :page-size="pageSize" :total="total" 
          layout="total, prev, pager, next" @current-change="loadUsers" />
      </div>
      
      <!-- 编辑用户弹窗 -->
      <el-dialog v-model="editDialogVisible" title="编辑用户" width="500px">
        <el-form :model="editForm" label-width="80px">
          <el-form-item label="昵称">
            <el-input v-model="editForm.nickname" />
          </el-form-item>
          <el-form-item label="邮箱">
            <el-input v-model="editForm.email" />
          </el-form-item>
          <el-form-item label="简介">
            <el-input v-model="editForm.bio" type="textarea" :rows="3" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveUser">保存</el-button>
        </template>
      </el-dialog>
      
      <!-- 权限设置弹窗 -->
      <el-dialog v-model="roleDialogVisible" title="权限设置" width="400px">
        <el-form :model="roleForm" label-width="80px">
          <el-form-item label="用户">
            <span>{{ roleForm.nickname }} (@{{ roleForm.username }})</span>
          </el-form-item>
          <el-form-item label="角色">
            <el-radio-group v-model="roleForm.role">
              <el-radio value="USER">普通用户</el-radio>
              <el-radio value="REVIEWER">审核员</el-radio>
              <el-radio value="ADMIN">管理员</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item>
            <div class="role-tips">
              <p><strong>普通用户：</strong>可浏览、发布文章、评论等基本功能</p>
              <p><strong>审核员：</strong>可审核文章、处理过时举报</p>
              <p><strong>管理员：</strong>拥有所有管理权限</p>
            </div>
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="roleDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveRole">保存</el-button>
        </template>
      </el-dialog>
      
      <!-- 添加用户弹窗 -->
      <el-dialog v-model="addDialogVisible" title="添加用户" width="500px">
        <el-form :model="addForm" :rules="addRules" ref="addFormRef" label-width="80px">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="addForm.username" placeholder="请输入用户名" />
          </el-form-item>
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="addForm.email" placeholder="请输入邮箱" />
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input v-model="addForm.password" type="password" placeholder="请输入密码（至少6位）" show-password />
          </el-form-item>
          <el-form-item label="角色" prop="role">
            <el-select v-model="addForm.role" style="width: 100%">
              <el-option label="普通用户" value="USER" />
              <el-option label="审核员" value="REVIEWER" />
              <el-option label="管理员" value="ADMIN" />
            </el-select>
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="addDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="createUser" :loading="addLoading">创建</el-button>
        </template>
      </el-dialog>
      
      <!-- 重置密码弹窗 -->
      <el-dialog v-model="passwordDialogVisible" title="重置密码" width="400px">
        <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-width="80px">
          <el-form-item label="用户">
            <span>{{ passwordForm.nickname }} (@{{ passwordForm.username }})</span>
          </el-form-item>
          <el-form-item label="新密码" prop="password">
            <el-input v-model="passwordForm.password" type="password" placeholder="请输入新密码（至少6位）" show-password />
          </el-form-item>
          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input v-model="passwordForm.confirmPassword" type="password" placeholder="请再次输入新密码" show-password />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="passwordDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="resetPassword" :loading="passwordLoading">确认重置</el-button>
        </template>
      </el-dialog>
    </div>
  </AdminLayout>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus } from '@element-plus/icons-vue'
import AdminLayout from './AdminLayout.vue'
import request from '@/api/index'

const users = ref([])
const loading = ref(false)
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const searchKeyword = ref('')
const filterRole = ref('')
const filterStatus = ref('')

const editDialogVisible = ref(false)
const editForm = reactive({ id: null, nickname: '', email: '', bio: '' })

const roleDialogVisible = ref(false)
const roleForm = reactive({ id: null, username: '', nickname: '', role: '' })

// 添加用户相关
const addDialogVisible = ref(false)
const addLoading = ref(false)
const addFormRef = ref(null)
const addForm = reactive({ username: '', email: '', password: '', role: 'USER' })
const addRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少6位', trigger: 'blur' }
  ],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }]
}

// 重置密码相关
const passwordDialogVisible = ref(false)
const passwordLoading = ref(false)
const passwordFormRef = ref(null)
const passwordForm = reactive({ id: null, username: '', nickname: '', password: '', confirmPassword: '' })
const validateConfirmPassword = (rule, value, callback) => {
  if (value !== passwordForm.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}
const passwordRules = {
  password: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleString('zh-CN')
}

const getRoleText = (role) => {
  const roleMap = { ADMIN: '管理员', REVIEWER: '审核员', USER: '用户' }
  return roleMap[role] || '用户'
}

const getRoleTagType = (role) => {
  const typeMap = { ADMIN: 'danger', REVIEWER: 'warning', USER: 'info' }
  return typeMap[role] || 'info'
}

const loadUsers = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/users', {
      params: { page: page.value, size: pageSize.value, keyword: searchKeyword.value, role: filterRole.value, status: filterStatus.value }
    })
    users.value = res.data.list || []
    total.value = res.data.total || 0
  } catch (e) { console.error(e) }
  finally { loading.value = false }
}

const handleSearch = () => { page.value = 1; loadUsers() }

const openEditDialog = (user) => {
  Object.assign(editForm, { id: user.id, nickname: user.nickname, email: user.email, bio: user.bio })
  editDialogVisible.value = true
}

const saveUser = async () => {
  try {
    await request.put(`/admin/users/${editForm.id}`, editForm)
    ElMessage.success('保存成功')
    editDialogVisible.value = false
    loadUsers()
  } catch (e) { ElMessage.error('保存失败') }
}

const toggleStatus = async (user) => {
  const action = user.status === 1 ? '禁用' : '启用'
  await ElMessageBox.confirm(`确定要${action}用户 ${user.nickname} 吗？`, '提示', { type: 'warning' })
  try {
    await request.put(`/admin/users/${user.id}/status`, { status: user.status === 1 ? 0 : 1 })
    ElMessage.success(`${action}成功`)
    loadUsers()
  } catch (e) { ElMessage.error(`${action}失败`) }
}

const openRoleDialog = (user) => {
  Object.assign(roleForm, { id: user.id, username: user.username, nickname: user.nickname, role: user.role })
  roleDialogVisible.value = true
}

const saveRole = async () => {
  try {
    await request.put(`/admin/users/${roleForm.id}/role`, { role: roleForm.role })
    ElMessage.success('权限设置成功')
    roleDialogVisible.value = false
    loadUsers()
  } catch (e) { ElMessage.error('设置失败') }
}

// 添加用户
const openAddDialog = () => {
  Object.assign(addForm, { username: '', email: '', password: '', role: 'USER' })
  addDialogVisible.value = true
}

const createUser = async () => {
  if (!addFormRef.value) return
  await addFormRef.value.validate()
  addLoading.value = true
  try {
    const res = await request.post('/admin/users', addForm)
    if (res.code === 200) {
      ElMessage.success('用户创建成功')
      addDialogVisible.value = false
      loadUsers()
    } else {
      ElMessage.error(res.message || '创建失败')
    }
  } catch (e) { ElMessage.error('创建失败') }
  finally { addLoading.value = false }
}

// 重置密码
const openPasswordDialog = (user) => {
  Object.assign(passwordForm, { id: user.id, username: user.username, nickname: user.nickname, password: '', confirmPassword: '' })
  passwordDialogVisible.value = true
}

const resetPassword = async () => {
  if (!passwordFormRef.value) return
  await passwordFormRef.value.validate()
  passwordLoading.value = true
  try {
    await request.put(`/admin/users/${passwordForm.id}/password`, { password: passwordForm.password })
    ElMessage.success('密码重置成功')
    passwordDialogVisible.value = false
  } catch (e) { ElMessage.error('重置失败') }
  finally { passwordLoading.value = false }
}

onMounted(() => loadUsers())
</script>

<style scoped>
.user-manage { background: #fff; border-radius: 8px; padding: 20px; }
.toolbar { margin-bottom: 20px; }
.search-box { display: flex; gap: 12px; }
.search-box .el-input { width: 240px; }
.user-cell { display: flex; align-items: center; gap: 12px; }
.user-info { display: flex; flex-direction: column; }
.nickname { font-weight: 500; color: #333; }
.username { font-size: 12px; color: #999; }
.pagination { margin-top: 20px; display: flex; justify-content: flex-end; }
.role-tips { font-size: 12px; color: #666; line-height: 1.8; background: #f5f7fa; padding: 12px; border-radius: 4px; }
.role-tips p { margin: 0; }
.role-tips strong { color: #333; }
</style>
