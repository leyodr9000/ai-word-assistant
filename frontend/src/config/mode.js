/**
 * 运行模式开关。
 * VITE_STANDALONE=1 构建出的"单机版"完全不走后端:
 * - 词书/学习进度/学习时长全部存浏览器本地
 * - 无需登录, 打开即用
 * - AI 助教由浏览器直连大模型接口 (Key 存本地)
 * 服务端版(默认)行为完全不变。
 */
export const STANDALONE = import.meta.env.VITE_STANDALONE === '1'

/** 单机版本地 AI 配置的存取 */
const AI_CONFIG_KEY = 'local_ai_config'

export function getLocalAiConfig() {
  try {
    const raw = localStorage.getItem(AI_CONFIG_KEY)
    if (!raw) return null
    const cfg = JSON.parse(raw)
    if (!cfg || !cfg.baseUrl || !cfg.apiKey || !cfg.modelName) return null
    return cfg
  } catch (e) {
    return null
  }
}

export function hasLocalAiConfig() {
  return getLocalAiConfig() !== null
}

export function saveLocalAiConfig(cfg) {
  localStorage.setItem(AI_CONFIG_KEY, JSON.stringify(cfg))
}
