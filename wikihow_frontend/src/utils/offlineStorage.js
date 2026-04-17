/**
 * 离线存储工具类
 * 使用 IndexedDB 存储收藏的文章以支持离线查看
 */

const DB_NAME = 'wikihow_offline'
const DB_VERSION = 1
const STORE_NAME = 'articles'

let db = null

/**
 * 深度克隆并清理对象，移除无法序列化的数据
 * @param {any} obj - 要清理的对象
 * @param {WeakSet} seen - 用于检测循环引用
 * @returns {any} 清理后的对象
 */
function deepCleanForStorage(obj, seen = new WeakSet()) {
  // 处理基本类型
  if (obj === null || obj === undefined) {
    return obj
  }

  const type = typeof obj

  // 基本类型直接返回
  if (type === 'string' || type === 'number' || type === 'boolean') {
    return obj
  }

  // 函数无法存储
  if (type === 'function') {
    return undefined
  }

  // 处理 Date
  if (obj instanceof Date) {
    return obj.toISOString()
  }

  // 处理数组
  if (Array.isArray(obj)) {
    if (seen.has(obj)) {
      return undefined // 循环引用
    }
    seen.add(obj)
    return obj.map((item) => deepCleanForStorage(item, seen)).filter((item) => item !== undefined)
  }

  // 处理普通对象
  if (type === 'object') {
    if (seen.has(obj)) {
      return undefined // 循环引用
    }
    seen.add(obj)

    const cleaned = {}
    for (const key of Object.keys(obj)) {
      const value = obj[key]
      const cleanedValue = deepCleanForStorage(value, seen)
      if (cleanedValue !== undefined) {
        cleaned[key] = cleanedValue
      }
    }
    return cleaned
  }

  return undefined
}

/**
 * 打开/初始化数据库
 */
export function openDatabase() {
  return new Promise((resolve, reject) => {
    if (db) {
      resolve(db)
      return
    }

    const request = indexedDB.open(DB_NAME, DB_VERSION)

    request.onerror = () => {
      console.error('打开 IndexedDB 失败:', request.error)
      reject(request.error)
    }

    request.onsuccess = () => {
      db = request.result
      resolve(db)
    }

    request.onupgradeneeded = (event) => {
      const database = event.target.result

      // 创建文章存储对象
      if (!database.objectStoreNames.contains(STORE_NAME)) {
        const store = database.createObjectStore(STORE_NAME, { keyPath: 'id' })
        // 创建索引
        store.createIndex('cachedAt', 'cachedAt', { unique: false })
        store.createIndex('title', 'title', { unique: false })
      }
    }
  })
}

/**
 * 保存文章到离线存储
 * @param {Object} article - 文章对象
 */
export async function saveArticleOffline(article) {
  try {
    const database = await openDatabase()

    return new Promise((resolve, reject) => {
      const transaction = database.transaction([STORE_NAME], 'readwrite')
      const store = transaction.objectStore(STORE_NAME)

      // 深度清理文章对象，移除无法序列化的数据
      const cleanedArticle = deepCleanForStorage(article)

      // 添加缓存时间戳
      const articleToSave = {
        ...cleanedArticle,
        cachedAt: new Date().toISOString(),
      }

      const request = store.put(articleToSave)

      request.onsuccess = () => {
        console.log('文章已缓存到离线存储:', article.id)
        resolve(true)
      }

      request.onerror = () => {
        console.error('缓存文章失败:', request.error)
        reject(request.error)
      }
    })
  } catch (error) {
    console.error('保存离线文章失败:', error)
    throw error
  }
}

/**
 * 从离线存储获取文章
 * @param {number} articleId - 文章ID
 */
export async function getArticleOffline(articleId) {
  try {
    const database = await openDatabase()
    
    return new Promise((resolve, reject) => {
      const transaction = database.transaction([STORE_NAME], 'readonly')
      const store = transaction.objectStore(STORE_NAME)
      const request = store.get(Number(articleId))
      
      request.onsuccess = () => {
        resolve(request.result || null)
      }
      
      request.onerror = () => {
        console.error('获取离线文章失败:', request.error)
        reject(request.error)
      }
    })
  } catch (error) {
    console.error('获取离线文章失败:', error)
    return null
  }
}

/**
 * 删除离线存储的文章
 * @param {number} articleId - 文章ID
 */
export async function removeArticleOffline(articleId) {
  try {
    const database = await openDatabase()
    
    return new Promise((resolve, reject) => {
      const transaction = database.transaction([STORE_NAME], 'readwrite')
      const store = transaction.objectStore(STORE_NAME)
      const request = store.delete(Number(articleId))
      
      request.onsuccess = () => {
        console.log('已从离线存储删除文章:', articleId)
        resolve(true)
      }
      
      request.onerror = () => {
        console.error('删除离线文章失败:', request.error)
        reject(request.error)
      }
    })
  } catch (error) {
    console.error('删除离线文章失败:', error)
    throw error
  }
}

/**
 * 获取所有离线存储的文章
 */
export async function getAllOfflineArticles() {
  try {
    const database = await openDatabase()
    
    return new Promise((resolve, reject) => {
      const transaction = database.transaction([STORE_NAME], 'readonly')
      const store = transaction.objectStore(STORE_NAME)
      const request = store.getAll()
      
      request.onsuccess = () => {
        resolve(request.result || [])
      }
      
      request.onerror = () => {
        console.error('获取所有离线文章失败:', request.error)
        reject(request.error)
      }
    })
  } catch (error) {
    console.error('获取所有离线文章失败:', error)
    return []
  }
}

/**
 * 检查文章是否已缓存
 * @param {number} articleId - 文章ID
 */
export async function isArticleCached(articleId) {
  const article = await getArticleOffline(articleId)
  return article !== null
}

/**
 * 清除所有离线缓存
 */
export async function clearAllOfflineArticles() {
  try {
    const database = await openDatabase()
    
    return new Promise((resolve, reject) => {
      const transaction = database.transaction([STORE_NAME], 'readwrite')
      const store = transaction.objectStore(STORE_NAME)
      const request = store.clear()
      
      request.onsuccess = () => {
        console.log('已清除所有离线缓存')
        resolve(true)
      }
      
      request.onerror = () => {
        console.error('清除离线缓存失败:', request.error)
        reject(request.error)
      }
    })
  } catch (error) {
    console.error('清除离线缓存失败:', error)
    throw error
  }
}

/**
 * 获取离线缓存统计信息
 */
export async function getOfflineStats() {
  try {
    const articles = await getAllOfflineArticles()
    return {
      count: articles.length,
      articles: articles.map(a => ({
        id: a.id,
        title: a.title,
        cachedAt: a.cachedAt
      }))
    }
  } catch (error) {
    console.error('获取离线统计失败:', error)
    return { count: 0, articles: [] }
  }
}

/**
 * 检查是否处于离线状态
 */
export function isOffline() {
  return !navigator.onLine
}

/**
 * 监听网络状态变化
 * @param {Function} onOnline - 上线回调
 * @param {Function} onOffline - 离线回调
 */
export function watchNetworkStatus(onOnline, onOffline) {
  window.addEventListener('online', onOnline)
  window.addEventListener('offline', onOffline)
  
  // 返回清理函数
  return () => {
    window.removeEventListener('online', onOnline)
    window.removeEventListener('offline', onOffline)
  }
}
