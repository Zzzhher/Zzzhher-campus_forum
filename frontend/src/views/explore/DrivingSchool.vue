<script setup>
import { ref } from 'vue';
import { School, Phone, Location, Star, Discount, Check } from '@element-plus/icons-vue';

// 驾校合作数据
const drivingSchools = ref([
  {
    id: 1,
    name: '通安驾校',
    logo: '/static/school1.png',
    rating: 4.8,
    students: 1200,
    price: '2980元起',
    originalPrice: '3980元',
    features: ['校内练车', '一对一教学', '快速拿证'],
    phone: '400-123-4567',
    address: '校园东门旁',
    tags: ['官方合作', '学生优惠'],
    description: '通安驾校是本校官方合作驾校，拥有专业的教练团队和完善的教学设施，为在校学生提供优质的学车服务。'
  },
  {
    id: 2,
    name: '安心驾校',
    logo: '/static/school2.png',
    rating: 4.6,
    students: 800,
    price: '2680元起',
    originalPrice: '3680元',
    features: ['周末班', '晚班', '免费接送'],
    phone: '400-234-5678',
    address: '校园南门对面',
    tags: ['性价比高', '灵活时间'],
    description: '安心驾校专注于学生群体，提供灵活的学车时间安排，支持周末班和晚班，方便学生课余时间学车。'
  },
  {
    id: 3,
    name: '恒通驾校',
    logo: '/static/school3.png',
    rating: 4.9,
    students: 1500,
    price: '3280元起',
    originalPrice: '4280元',
    features: ['VIP服务', '包过班', '模拟考试'],
    phone: '400-345-6789',
    address: '校园西门附近',
    tags: ['高端服务', '通过率高'],
    description: '恒通驾校提供VIP学车服务，拥有最高的考试通过率，提供模拟考试和包过班服务，适合追求高品质学车体验的学生。'
  }
]);

// 优惠活动
const promotions = ref([
  {
    id: 1,
    title: '新生专享优惠',
    discount: '立减500元',
    condition: '凭学生证报名',
    endDate: '长期有效',
    color: '#ff6b6b'
  },
  {
    id: 2,
    title: '团报优惠',
    discount: '3人成团各减300',
    condition: '3人及以上同时报名',
    endDate: '长期有效',
    color: '#4ecdc4'
  },
  {
    id: 3,
    title: '老带新奖励',
    discount: '推荐奖励200元',
    condition: '老学员推荐新学员',
    endDate: '长期有效',
    color: '#45b7d1'
  }
]);

// 学车攻略
const tips = ref([
  {
    title: '如何选择合适的驾校？',
    content: '选择驾校时要考虑：距离、价格、口碑、通过率等因素。建议实地考察后再做决定。'
  },
  {
    title: '学车需要准备什么？',
    content: '身份证原件、学生证、体检报告、一寸照片等。建议提前准备好相关材料。'
  },
  {
    title: '学车时间安排建议',
    content: '建议利用课余时间和周末学车，避开考试周。合理安排学车时间，避免影响学业。'
  },
  {
    title: '考试流程介绍',
    content: '科目一（理论）→科目二（场地）→科目三（路考）→科目四（安全文明）。每个科目都需要认真准备。'
  }
]);

const contactSchool = (school) => {
  // 这里可以添加联系驾校的逻辑
  console.log('联系驾校:', school.name);
};
</script>

<template>
  <div class="driving-school">
    <!-- 页面标题 -->
    <el-card shadow="hover" class="header-card">
      <div class="header-content">
        <el-icon :size="40" color="#409EFF"><School /></el-icon>
        <div class="header-text">
          <h2>校园驾校</h2>
          <p>官方合作驾校，为学生提供优质学车服务</p>
        </div>
      </div>
    </el-card>

    <!-- 优惠活动 -->
    <el-card shadow="hover" class="promotions-card">
      <template #header>
        <div class="card-header">
          <span class="header-title">
            <el-icon><Discount /></el-icon>
            限时优惠活动
          </span>
        </div>
      </template>
      <div class="promotions-list">
        <el-card
          v-for="promo in promotions"
          :key="promo.id"
          class="promo-item"
          :style="{ borderLeft: `4px solid ${promo.color}` }"
        >
          <div class="promo-content">
            <div class="promo-title">{{ promo.title }}</div>
            <div class="promo-discount" :style="{ color: promo.color }">{{ promo.discount }}</div>
            <div class="promo-condition">{{ promo.condition }}</div>
            <div class="promo-date">有效期至：{{ promo.endDate }}</div>
          </div>
        </el-card>
      </div>
    </el-card>

    <!-- 驾校列表 -->
    <el-card shadow="hover" class="schools-card">
      <template #header>
        <div class="card-header">
          <span class="header-title">
            <el-icon><School /></el-icon>
            合作驾校
          </span>
        </div>
      </template>
      <div class="schools-list">
        <el-card
          v-for="school in drivingSchools"
          :key="school.id"
          shadow="hover"
          class="school-item"
        >
          <div class="school-header">
            <div class="school-info">
              <el-avatar :size="80" :src="school.logo" />
              <div class="school-basic">
                <h3>{{ school.name }}</h3>
                <div class="school-rating">
                  <el-rate v-model="school.rating" disabled show-score text-color="#ff9900" />
                  <span class="students-count">{{ school.students }}人已报名</span>
                </div>
                <div class="school-tags">
                  <el-tag
                    v-for="tag in school.tags"
                    :key="tag"
                    size="small"
                    type="success"
                    effect="plain"
                  >
                    {{ tag }}
                  </el-tag>
                </div>
              </div>
            </div>
            <div class="school-price">
              <div class="current-price">{{ school.price }}</div>
              <div class="original-price">原价：{{ school.originalPrice }}</div>
            </div>
          </div>
          
          <div class="school-features">
            <div
              v-for="feature in school.features"
              :key="feature"
              class="feature-item"
            >
              <el-icon color="#67C23A"><Check /></el-icon>
              <span>{{ feature }}</span>
            </div>
          </div>
          
          <div class="school-description">
            {{ school.description }}
          </div>
          
          <div class="school-contact">
            <div class="contact-item">
              <el-icon><Phone /></el-icon>
              <span>{{ school.phone }}</span>
            </div>
            <div class="contact-item">
              <el-icon><Location /></el-icon>
              <span>{{ school.address }}</span>
            </div>
            <el-button type="primary" @click="contactSchool(school)">
              立即咨询
            </el-button>
          </div>
        </el-card>
      </div>
    </el-card>

    <!-- 学车攻略 -->
    <el-card shadow="hover" class="tips-card">
      <template #header>
        <div class="card-header">
          <span class="header-title">
            <el-icon><Star /></el-icon>
            学车攻略
          </span>
        </div>
      </template>
      <el-collapse>
        <el-collapse-item
          v-for="(tip, index) in tips"
          :key="index"
          :title="tip.title"
          :name="index"
        >
          <p>{{ tip.content }}</p>
        </el-collapse-item>
      </el-collapse>
    </el-card>

    <!-- 免责声明 -->
    <el-card shadow="hover" class="disclaimer-card">
      <el-alert
        title="温馨提示"
        type="info"
        :closable="false"
        show-icon
      >
        <template #default>
          <p>1. 以上驾校均为论坛合作机构，报名前请仔细核实相关信息</p>
          <p>2. 价格和优惠活动可能会有变动，请以驾校实际报价为准</p>
          <p>3. 学车过程中如有任何问题，请及时与驾校沟通解决</p>
          <p>4. 本页面信息仅供参考，不构成任何商业承诺</p>
        </template>
      </el-alert>
    </el-card>
  </div>
</template>

<style lang="less" scoped>
.driving-school {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;

  .header-card {
    margin-bottom: 20px;

    .header-content {
      display: flex;
      align-items: center;
      gap: 20px;

      .header-text {
        h2 {
          margin: 0 0 8px 0;
          color: #303133;
        }

        p {
          margin: 0;
          color: #909399;
          font-size: 14px;
        }
      }
    }
  }

  .card-header {
    .header-title {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 16px;
      font-weight: bold;
      color: #303133;
    }
  }

  .promotions-card {
    margin-bottom: 20px;

    .promotions-list {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
      gap: 15px;

      .promo-item {
        .promo-content {
          .promo-title {
            font-size: 16px;
            font-weight: bold;
            color: #303133;
            margin-bottom: 8px;
          }

          .promo-discount {
            font-size: 20px;
            font-weight: bold;
            margin-bottom: 8px;
          }

          .promo-condition {
            font-size: 14px;
            color: #606266;
            margin-bottom: 4px;
          }

          .promo-date {
            font-size: 12px;
            color: #909399;
          }
        }
      }
    }
  }

  .schools-card {
    margin-bottom: 20px;

    .schools-list {
      display: flex;
      flex-direction: column;
      gap: 20px;

      .school-item {
        .school-header {
          display: flex;
          justify-content: space-between;
          align-items: flex-start;
          margin-bottom: 15px;

          .school-info {
            display: flex;
            gap: 15px;

            .school-basic {
              h3 {
                margin: 0 0 8px 0;
                color: #303133;
              }

              .school-rating {
                display: flex;
                align-items: center;
                gap: 10px;
                margin-bottom: 8px;

                .students-count {
                  font-size: 12px;
                  color: #909399;
                }
              }

              .school-tags {
                display: flex;
                gap: 8px;
                flex-wrap: wrap;
              }
            }
          }

          .school-price {
            text-align: right;

            .current-price {
              font-size: 24px;
              font-weight: bold;
              color: #f56c6c;
            }

            .original-price {
              font-size: 14px;
              color: #909399;
              text-decoration: line-through;
            }
          }
        }

        .school-features {
          display: flex;
          gap: 20px;
          margin-bottom: 15px;
          flex-wrap: wrap;

          .feature-item {
            display: flex;
            align-items: center;
            gap: 5px;
            font-size: 14px;
            color: #606266;
          }
        }

        .school-description {
          font-size: 14px;
          color: #606266;
          line-height: 1.6;
          margin-bottom: 15px;
          padding: 10px;
          background-color: #f5f7fa;
          border-radius: 4px;
        }

        .school-contact {
          display: flex;
          align-items: center;
          gap: 20px;
          flex-wrap: wrap;

          .contact-item {
            display: flex;
            align-items: center;
            gap: 5px;
            font-size: 14px;
            color: #606266;
          }
        }
      }
    }
  }

  .tips-card {
    margin-bottom: 20px;
  }

  .disclaimer-card {
    p {
      margin: 5px 0;
      font-size: 13px;
    }
  }
}
</style>
