<template>
  <div class="landing-page">
    <ConcertHero
      v-if="concert"
      :title="concert.title"
      :description="concert.description"
      :date="formattedDate"
      :venue="concert.venue"
      @cta="openModal"
    />

    <!-- Program Section -->
    <section class="py-5 py-md-4 py-sm-3 bg-body">
      <div class="container">
        <div class="text-center mb-5 mb-md-4 mb-sm-3">
          <h2 class="display-5 fw-bold mb-3 mb-md-2 mb-sm-2 program-title">Программа концерта</h2>
          <p class="lead text-body-secondary program-subtitle">Расписание мероприятия для участников</p>
        </div>

        <div class="program-timeline">
          <!-- 18:00 - Фуршет -->
          <div class="timeline-item">
            <div class="timeline-marker">
              <div class="timeline-time">18:00</div>
              <div class="timeline-dot"></div>
            </div>
            <div class="timeline-content">
              <div class="program-card">
                <div class="program-icon">
                  <i class="bi bi-cup-hot fs-2"></i>
                </div>
                <h3 class="h4 fw-bold mb-2">Фуршет</h3>
                <p class="text-body-secondary mb-0">Встреча гостей, легкие закуски и напитки</p>
              </div>
            </div>
          </div>

          <!-- 19:00-22:00 - Концертная программа -->
          <div class="timeline-item">
            <div class="timeline-marker">
              <div class="timeline-time">19:00 - 22:00</div>
              <div class="timeline-dot timeline-dot-main"></div>
            </div>
            <div class="timeline-content">
              <div class="program-card program-card-main">
                <div class="program-icon program-icon-main">
                  <i class="bi bi-music-note-beamed fs-2"></i>
                </div>
                <h3 class="h4 fw-bold mb-3">Концертная программа</h3>
                <div class="row g-3">
                  <div class="col-md-6" v-for="item in concertProgram" :key="item.title">
                    <div class="program-item-flip">
                      <div class="program-item-flip-inner">
                        <!-- Front side -->
                        <div class="program-item-flip-front">
                          <div class="program-item-icon">
                            <i :class="['bi', item.icon]"></i>
                          </div>
                          <div class="program-item-content">
                            <h4 class="h6 fw-semibold mb-1">{{ item.title }}</h4>
                            <p class="small text-body-secondary mb-0">{{ item.description }}</p>
                          </div>
                        </div>
                        <!-- Back side -->
                        <div class="program-item-flip-back" v-if="item.details">
                          <div class="program-item-back-content">
                            <h4 class="h6 fw-semibold mb-2">{{ item.title }}</h4>
                            <ul class="program-details-list">
                              <li v-for="detail in item.details" :key="detail">{{ detail }}</li>
                            </ul>
                            <div v-if="item.groups" class="program-groups">
                              <p class="small fw-semibold mb-1 text-primary">Группы танцоров:</p>
                              <p class="small mb-0">{{ item.groups }}</p>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Partners Section -->
    <section class="py-5 py-md-4 py-sm-3 bg-light">
      <div class="container">
        <div class="text-center mb-5 mb-md-4 mb-sm-3">
          <h2 class="display-5 fw-bold mb-3 mb-md-2 mb-sm-2">Наши партнёры</h2>
          <p class="lead text-body-secondary">Благодарим наших спонсоров за поддержку мероприятия</p>
        </div>

        <!-- General Sponsors -->
        <div v-if="generalSponsors.length > 0" class="mb-5 mb-md-4 mb-sm-3">
          <div class="text-center mb-4 mb-md-3 mb-sm-2">
            <h3 class="h4 fw-bold text-primary mb-2">
              <i class="bi bi-star-fill me-2"></i>
              Генеральный спонсор
            </h3>
            <p class="text-body-secondary small mb-0">Главные партнёры нашего концерта</p>
          </div>
          <!-- Desktop: Grid layout -->
          <div class="row g-4 g-md-3 g-sm-2 justify-content-center d-none d-md-flex">
            <div 
              v-for="(sponsor, index) in generalSponsors" 
              :key="`sponsor-${sponsor.name}-${index}`"
              class="col-lg-6 col-md-6 partner-sponsor-wrapper partner-card-animated"
            >
              <div class="partner-card partner-card-general" @click="openSponsorModal(sponsor)">
                <div class="partner-logo-wrapper" @mouseenter="hoveredSponsor = index" @mouseleave="hoveredSponsor = null">
                  <div class="partner-logo partner-logo-general">
                    <img 
                      v-if="sponsor.logo" 
                      :src="sponsor.logo" 
                      :alt="sponsor.name"
                      class="partner-logo-img"
                    />
                    <span v-else class="partner-logo-text">{{ sponsor.name }}</span>
                  </div>
                </div>
                <div class="partner-badge-general">
                  <i class="bi bi-award-fill"></i>
                  Генеральный спонсор
                </div>
              </div>
              
              <!-- Info Tooltip (outside card to avoid overflow issues) -->
              <div 
                v-if="sponsor.description && hoveredSponsor === index" 
                class="partner-info-tooltip"
                @mouseenter="hoveredSponsor = index"
                @mouseleave="hoveredSponsor = null"
              >
                <div class="partner-info-content">
                  <h4 class="partner-info-title">{{ sponsor.name }}</h4>
                  <p class="partner-info-description">{{ sponsor.description }}</p>
                  
                  <div v-if="sponsor.services && sponsor.services.length > 0" class="partner-info-services">
                    <h5 class="partner-info-services-title">Наши услуги:</h5>
                    <ul class="partner-info-services-list">
                      <li v-for="(service, i) in sponsor.services" :key="i">{{ service }}</li>
                    </ul>
                  </div>
                  
                  <div v-if="sponsor.telegram" class="partner-info-telegram">
                    <a 
                      :href="sponsor.telegram" 
                      target="_blank" 
                      rel="noopener noreferrer"
                      class="partner-telegram-link"
                    >
                      <i class="bi bi-telegram"></i>
                      {{ sponsor.telegramChannel || 'Наш Telegram канал' }}
                    </a>
                  </div>
                </div>
                <div class="partner-info-arrow"></div>
              </div>
            </div>
          </div>
          
          <!-- Mobile: Horizontal scroll -->
          <div class="partners-scroll-container d-md-none">
            <div class="partners-scroll-wrapper partners-sponsors-scroll">
              <div 
                v-for="(sponsor, index) in generalSponsors" 
                :key="`sponsor-mobile-${sponsor.name}-${index}`"
                class="partner-sponsor-wrapper partner-sponsor-mobile partner-card-animated"
              >
                <div class="partner-card partner-card-general" @click="openSponsorModal(sponsor)">
                  <div class="partner-logo-wrapper" @mouseenter="hoveredSponsor = index" @mouseleave="hoveredSponsor = null">
                    <div class="partner-logo partner-logo-general">
                      <img 
                        v-if="sponsor.logo" 
                        :src="sponsor.logo" 
                        :alt="sponsor.name"
                        class="partner-logo-img"
                      />
                      <span v-else class="partner-logo-text">{{ sponsor.name }}</span>
                    </div>
                  </div>
                  <div class="partner-badge-general">
                    <i class="bi bi-award-fill"></i>
                    Генеральный спонсор
                  </div>
                </div>
                
                <!-- Info Tooltip for mobile -->
                <div 
                  v-if="sponsor.description && hoveredSponsor === index" 
                  class="partner-info-tooltip partner-info-tooltip-mobile"
                  @mouseenter="hoveredSponsor = index"
                  @mouseleave="hoveredSponsor = null"
                >
                  <div class="partner-info-content">
                    <h4 class="partner-info-title">{{ sponsor.name }}</h4>
                    <p class="partner-info-description">{{ sponsor.description }}</p>
                    
                    <div v-if="sponsor.services && sponsor.services.length > 0" class="partner-info-services">
                      <h5 class="partner-info-services-title">Наши услуги:</h5>
                      <ul class="partner-info-services-list">
                        <li v-for="(service, i) in sponsor.services" :key="i">{{ service }}</li>
                      </ul>
                    </div>
                    
                    <div v-if="sponsor.telegram" class="partner-info-telegram">
                      <a 
                        :href="sponsor.telegram" 
                        target="_blank" 
                        rel="noopener noreferrer"
                        class="partner-telegram-link"
                      >
                        <i class="bi bi-telegram"></i>
                        {{ sponsor.telegramChannel || 'Наш Telegram канал' }}
                      </a>
                    </div>
                  </div>
                  <div class="partner-info-arrow"></div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Event Partners -->
        <div v-if="eventPartners.length > 0">
          <div class="text-center mb-4 mb-md-3 mb-sm-2">
            <h3 class="h5 fw-semibold text-body mb-2">
              <i class="bi bi-handshake me-2"></i>
              Партнёры события
            </h3>
            <p class="text-body-secondary small mb-0">Наши официальные партнёры</p>
          </div>
          <!-- Grid layout for all screens -->
          <div class="row g-3 g-md-4 justify-content-center">
            <div 
              v-for="(partner, index) in eventPartners" 
              :key="`partner-${partner.name}-${index}`"
              class="col-lg-3 col-md-4 col-6 col-sm-6 partner-card-animated"
            >
              <div class="partner-card partner-card-event">
                <div class="partner-logo-wrapper">
                  <div class="partner-logo partner-logo-event">
                    <span class="partner-logo-text">{{ partner.name }}</span>
                  </div>
                </div>
                <div class="partner-badge-event">
                  <i class="bi bi-check-circle-fill"></i>
                  Партнёр
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- CTA Section -->
    <section class="py-5 bg-primary text-white">
      <div class="container text-center">
        <h2 class="display-6 fw-bold mb-3">Готовы выбрать место?</h2>
        <p class="lead mb-4">Выберите лучший стол на интерактивной карте зала</p>
        <button class="btn btn-light btn-lg px-5" @click="openModal">
          <i class="bi bi-ticket-perforated me-2"></i>
          Выбрать место
        </button>
      </div>
    </section>

    <SeatMapModal :open="modalOpen" :concert-id="concertId" @close="modalOpen = false" />

    <!-- Sponsor Info Modal -->
    <div v-if="selectedSponsor" class="sponsor-modal-overlay" @click="closeSponsorModal">
      <div class="sponsor-modal" @click.stop>
        <button class="sponsor-modal-close" @click="closeSponsorModal">
          <i class="bi bi-x-lg"></i>
        </button>
        <div class="sponsor-modal-content">
          <div class="sponsor-modal-header">
            <div v-if="selectedSponsor.logo" class="sponsor-modal-logo">
              <img :src="selectedSponsor.logo" :alt="selectedSponsor.name" />
            </div>
            <h3 class="sponsor-modal-title">{{ selectedSponsor.name }}</h3>
            <div class="sponsor-modal-badge">
              <i class="bi bi-award-fill"></i>
              Генеральный спонсор
            </div>
          </div>
          
          <div v-if="selectedSponsor.description" class="sponsor-modal-description">
            <p>{{ selectedSponsor.description }}</p>
          </div>
          
          <div v-if="selectedSponsor.services && selectedSponsor.services.length > 0" class="sponsor-modal-services">
            <h4 class="sponsor-modal-services-title">Наши услуги:</h4>
            <ul class="sponsor-modal-services-list">
              <li v-for="(service, i) in selectedSponsor.services" :key="i">
                <i class="bi bi-check-circle"></i>
                {{ service }}
              </li>
            </ul>
          </div>
          
          <div v-if="selectedSponsor.telegram" class="sponsor-modal-telegram">
            <a 
              :href="selectedSponsor.telegram" 
              target="_blank" 
              rel="noopener noreferrer"
              class="sponsor-telegram-link"
            >
              <i class="bi bi-telegram"></i>
              {{ selectedSponsor.telegramChannel || 'Наш Telegram канал' }}
            </a>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue';
import ConcertHero from '../components/ConcertHero.vue';
import SeatMapModal from '../components/SeatMapModal.vue';
import { useConcertStore } from '../stores/concertStore';
import { storeToRefs } from 'pinia';
import chidLogo from '../assets/chidlogo.png';

const concertId = 1;
const modalOpen = ref(false);
const hoveredSponsor = ref<number | null>(null);
const selectedSponsor = ref<any>(null);

const concertStore = useConcertStore();
const { concert } = storeToRefs(concertStore);

onMounted(() => {
  concertStore.load(concertId);
});

const openModal = () => {
  modalOpen.value = true;
};

const formattedDate = computed(() => {
  if (!concert.value) return '';
  return new Intl.DateTimeFormat('ru-RU', {
    dateStyle: 'full',
    timeStyle: 'short'
  }).format(new Date(concert.value.concertDate));
});

const concertProgram = [
  {
    title: 'Танцы',
    description: 'Зажигательные танцевальные номера',
    icon: 'bi-music-player',
    details: ['Памирский танцы', 'Сагдинский', 'Хатлонский'],
    groups: 'Выступления профессиональных танцевальных коллективов'
  },
  {
    title: 'Сытный банкетный стол',
    description: 'Богатое угощение для всех гостей',
    icon: 'bi-egg-fried',
    details: ['Традиционные блюда', 'Национальная кухня', 'Десерты и напитки']
  },
  {
    title: 'ШОУ-ПРОГРАММА',
    description: 'Яркие выступления и развлечения',
    icon: 'bi-stars',
    details: ['Музыкальные номера', 'Вокальные выступления', 'Интерактивные шоу']
  },
  {
    title: 'Розыгрыши призов и конкурсы',
    description: 'Интерактивные игры с ценными подарками',
    icon: 'bi-gift',
    details: ['Розыгрыш призов', 'Интерактивные конкурсы', 'Сюрпризы для гостей']
  }
];

// Partners data - original arrays
const originalGeneralSponsors = [
  { 
    name: '7 Агентство недвижимости',
    logo: chidLogo,
    description: 'Профессиональное агентство недвижимости с многолетним опытом работы на рынке. Мы помогаем найти дом мечты и обеспечить безопасные сделки.',
    telegram: 'https://t.me/agency7_realty',
    telegramChannel: '@agency7_realty',
    services: [
      'Продажа и покупка недвижимости',
      'Аренда жилой и коммерческой недвижимости',
      'Юридическое сопровождение сделок',
      'Оценка недвижимости',
      'Консультации по инвестициям'
    ]
  },
  { name: 'Спонсор 2' }
];

const originalEventPartners = [
  { name: 'Партнёр 1' },
  { name: 'Партнёр 2' },
  { name: 'Партнёр 3' },
  { name: 'Партнёр 4' }
];

// Reactive arrays for display
const generalSponsors = ref([...originalGeneralSponsors]);
const eventPartners = ref([...originalEventPartners]);

// Function to shuffle array
const shuffleArray = <T,>(array: T[]): T[] => {
  const shuffled = [...array];
  for (let i = shuffled.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1));
    [shuffled[i], shuffled[j]] = [shuffled[j], shuffled[i]];
  }
  return shuffled;
};

// Function to rotate partners
const rotatePartners = () => {
  // Rotate general sponsors (swap positions)
  if (generalSponsors.value.length >= 2) {
    generalSponsors.value = [generalSponsors.value[1], generalSponsors.value[0]];
  }
  
  // Shuffle event partners
  if (eventPartners.value.length > 1) {
    eventPartners.value = shuffleArray(eventPartners.value);
  }
};

// Start rotation interval
let rotationInterval: ReturnType<typeof setInterval> | null = null;

const startRotation = () => {
  if (rotationInterval) {
    clearInterval(rotationInterval);
  }
  rotationInterval = setInterval(rotatePartners, 15000);
};

const stopRotation = () => {
  if (rotationInterval) {
    clearInterval(rotationInterval);
    rotationInterval = null;
  }
};

const openSponsorModal = (sponsor: any) => {
  selectedSponsor.value = sponsor;
  stopRotation();
};

const closeSponsorModal = () => {
  selectedSponsor.value = null;
  startRotation();
};

onMounted(() => {
  concertStore.load(concertId);
  
  // Start rotating partners every 15 seconds
  startRotation();
});

// Cleanup interval on unmount
onUnmounted(() => {
  stopRotation();
});
</script>

<style scoped>
.landing-page {
  min-height: 100vh;
}

/* Program Timeline Styles */
.program-timeline {
  position: relative;
  max-width: 900px;
  margin: 0 auto;
  padding: 2rem 0;
}

.program-timeline::before {
  content: '';
  position: absolute;
  left: 120px;
  top: 0;
  bottom: 0;
  width: 3px;
  background: linear-gradient(180deg, #18723F 0%, #6f42c1 100%);
  border-radius: 2px;
}

.timeline-item {
  position: relative;
  display: flex;
  margin-bottom: 3rem;
  align-items: flex-start;
}

.timeline-item:last-child {
  margin-bottom: 0;
}

.timeline-marker {
  position: relative;
  width: 120px;
  flex-shrink: 0;
  padding-right: 2rem;
  text-align: right;
}

.timeline-time {
  font-size: 1.1rem;
  font-weight: 700;
  color: #18723F;
  margin-bottom: 0.5rem;
  white-space: nowrap;
}

.timeline-dot {
  position: absolute;
  right: -1.5rem;
  top: -1.75rem;
  width: 20px;
  height: 20px;
  background: #18723F;
  border: 4px solid #fff;
  border-radius: 50%;
  box-shadow: 0 2px 8px rgba(24, 114, 63, 0.3);
  z-index: 2;
}

.timeline-dot-main {
  width: 28px;
  height: 28px;
  right: -1.7rem;
  top: -1.85rem;
  background: linear-gradient(135deg, #18723F 0%, #6f42c1 100%);
  border-width: 5px;
  box-shadow: 0 4px 12px rgba(111, 66, 193, 0.4);
}

.timeline-content {
  flex: 1;
  padding-left: 1rem;
}

.program-card {
  background: #fff;
  border-radius: 16px;
  padding: 2rem;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  border: 1px solid rgba(24, 114, 63, 0.1);
}

.program-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
}

.program-card-main {
  background: linear-gradient(135deg, rgba(24, 114, 63, 0.05) 0%, rgba(111, 66, 193, 0.05) 100%);
  border: 2px solid rgba(24, 114, 63, 0.2);
}

.program-icon {
  width: 70px;
  height: 70px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, rgba(24, 114, 63, 0.1), rgba(111, 66, 193, 0.1));
  border-radius: 16px;
  color: #18723F;
  margin-bottom: 1rem;
}

.program-icon-main {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, #18723F, #6f42c1);
  color: #fff;
  margin-bottom: 1.5rem;
}

/* Flip Card Styles */
.program-item-flip {
  perspective: 1000px;
  height: 100%;
  min-height: 120px;
}

.program-item-flip-inner {
  position: relative;
  width: 100%;
  height: 100%;
  transition: transform 0.6s;
  transform-style: preserve-3d;
}

.program-item-flip:hover .program-item-flip-inner {
  transform: rotateY(180deg);
}

.program-item-flip-front,
.program-item-flip-back {
  position: absolute;
  width: 100%;
  height: 100%;
  backface-visibility: hidden;
  -webkit-backface-visibility: hidden;
  display: flex;
  align-items: flex-start;
  gap: 1rem;
  padding: 1rem;
  background: #fff;
  border-radius: 12px;
  border: 1px solid rgba(24, 114, 63, 0.1);
  transition: all 0.3s ease;
}

.program-item-flip-back {
  transform: rotateY(180deg);
  background: linear-gradient(135deg, rgba(24, 114, 63, 0.05) 0%, rgba(111, 66, 193, 0.05) 100%);
  border: 2px solid rgba(24, 114, 63, 0.2);
  align-items: flex-start;
  justify-content: flex-start;
  padding: 0.75rem;
  overflow-y: auto;
  overflow-x: hidden;
  max-height: 100%;
  scrollbar-width: thin;
  scrollbar-color: rgba(24, 114, 63, 0.3) transparent;
}

.program-item-flip-back::-webkit-scrollbar {
  width: 6px;
}

.program-item-flip-back::-webkit-scrollbar-track {
  background: transparent;
  border-radius: 10px;
}

.program-item-flip-back::-webkit-scrollbar-thumb {
  background: rgba(24, 114, 63, 0.3);
  border-radius: 10px;
}

.program-item-flip-back::-webkit-scrollbar-thumb:hover {
  background: rgba(24, 114, 63, 0.5);
}

.program-item-back-content {
  width: 100%;
  overflow: visible;
}

.program-item-back-content h4 {
  font-size: 0.95rem;
  margin-bottom: 0.5rem !important;
}

.program-details-list {
  list-style: none;
  padding: 0;
  margin: 0;
  margin-bottom: 0.5rem;
}

.program-details-list li {
  padding: 0.25rem 0;
  padding-left: 1rem;
  position: relative;
  color: #495057;
  font-size: 0.8rem;
  line-height: 1.3;
}

.program-details-list li::before {
  content: '•';
  position: absolute;
  left: 0;
  color: #18723F;
  font-weight: bold;
  font-size: 1rem;
  line-height: 1.2;
}

.program-groups {
  padding-top: 0.5rem;
  margin-top: 0.5rem;
  border-top: 1px solid rgba(24, 114, 63, 0.1);
}

.program-groups p {
  font-size: 0.75rem;
  margin-bottom: 0.25rem !important;
}

.program-groups .small {
  font-size: 0.7rem !important;
}

.program-item-icon {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, rgba(24, 114, 63, 0.1), rgba(111, 66, 193, 0.1));
  border-radius: 10px;
  color: #18723F;
  flex-shrink: 0;
  font-size: 1.2rem;
}

.program-item-content {
  flex: 1;
}

/* Responsive Design */
@media (max-width: 768px) {
  .program-timeline {
    padding: 1rem 0;
  }

  .program-timeline::before {
    left: 60px;
    width: 2px;
  }

  .timeline-item {
    margin-bottom: 2rem;
  }

  .timeline-marker {
    width: 60px;
    padding-right: 0.75rem;
  }

  .timeline-time {
    font-size: 0.85rem;
    margin-bottom: 0.25rem;
  }

  .timeline-dot {
    right: -0.75rem;
    width: 14px;
    height: 14px;
    border-width: 3px;
    top: -1.15rem;
  }

  .timeline-dot-main {
    right: -0.8rem;
    width: 18px;
    height: 18px;
    border-width: 3px;
    top: -1.2rem;
  }

  .timeline-content {
    padding-left: 0.25rem;
  }

  .program-card {
    padding: 1rem;
    border-radius: 12px;
  }

  .program-icon {
    width: 50px;
    height: 50px;
    margin-bottom: 0.75rem;
    border-radius: 12px;
  }

  .program-icon i {
    font-size: 1.25rem !important;
  }

  .program-icon-main {
    width: 60px;
    height: 60px;
    margin-bottom: 1rem;
  }

  .program-card h3 {
    font-size: 1.1rem;
    margin-bottom: 0.75rem !important;
  }

  .program-card p {
    font-size: 0.875rem;
  }

  .program-item-flip {
    min-height: 100px;
  }

  .program-item-flip-front,
  .program-item-flip-back {
    flex-direction: row;
    text-align: left;
    padding: 0.75rem;
    gap: 0.75rem;
    border-radius: 10px;
  }

  .program-item-flip-back {
    padding: 0.6rem;
    overflow-y: auto;
    overflow-x: hidden;
  }

  .program-item-back-content h4 {
    font-size: 0.85rem !important;
    margin-bottom: 0.4rem !important;
  }

  .program-item-icon {
    width: 32px;
    height: 32px;
    font-size: 1rem;
    border-radius: 8px;
    margin: 0;
  }

  .program-details-list {
    margin-bottom: 0.4rem;
  }

  .program-details-list li {
    font-size: 0.7rem;
    padding: 0.2rem 0;
    padding-left: 0.9rem;
    line-height: 1.2;
  }

  .program-details-list li::before {
    font-size: 0.9rem;
  }

  .program-groups {
    padding-top: 0.4rem;
    margin-top: 0.4rem;
  }

  .program-groups p {
    font-size: 0.7rem !important;
    margin-bottom: 0.2rem !important;
  }

  .program-groups .small {
    font-size: 0.65rem !important;
  }

  .program-item-content h4 {
    font-size: 0.9rem;
    margin-bottom: 0.25rem !important;
  }

  .program-item-content p {
    font-size: 0.8rem;
  }

  .program-title {
    font-size: 1.75rem !important;
  }

  .program-subtitle {
    font-size: 0.95rem !important;
  }

  .row.g-3 {
    --bs-gutter-y: 0.75rem;
    --bs-gutter-x: 0.75rem;
  }
}

/* Partners Section Styles */
.partner-card {
  position: relative;
  background: #fff;
  border-radius: 20px;
  padding: 2.5rem 2rem;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.6s cubic-bezier(0.25, 0.46, 0.45, 0.94);
  border: 2px solid transparent;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 200px;
  overflow: hidden;
}

.partner-card-general {
  overflow: visible;
}

.partner-card:hover {
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
}

/* General Sponsor Card */
.partner-card-general {
  background: linear-gradient(135deg, rgba(24, 114, 63, 0.05) 0%, rgba(111, 66, 193, 0.05) 100%);
  border: 3px solid rgba(24, 114, 63, 0.3);
  padding: 3rem 2.5rem;
  min-height: 280px;
  position: relative;
  cursor: pointer;
}

.partner-card-general::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #18723F 0%, #6f42c1 100%);
}

.partner-card-general:hover {
  border-color: rgba(24, 114, 63, 0.5);
  box-shadow: 0 16px 50px rgba(24, 114, 63, 0.2);
}

/* Event Partner Card */
.partner-card-event {
  border: 2px solid rgba(24, 114, 63, 0.15);
  min-height: 180px;
  padding: 2rem 1.5rem;
}

.partner-card-event:hover {
  border-color: rgba(24, 114, 63, 0.3);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.12);
}

.partner-logo-wrapper {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 1rem;
  position: relative;
}

.partner-logo {
  width: 100%;
  max-width: 100%;
  height: auto;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fff;
  border-radius: 16px;
  padding: 2rem;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.partner-logo::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.4), transparent);
  transition: left 0.5s ease;
}

.partner-card:hover .partner-logo::before {
  left: 100%;
}

.partner-logo-general {
  min-height: 180px;
  padding: 2.5rem;
  background: linear-gradient(135deg, #fff 0%, rgba(24, 114, 63, 0.02) 100%);
  border: 2px solid rgba(24, 114, 63, 0.1);
}

.partner-logo-event {
  min-height: 120px;
  padding: 1.5rem;
}

.partner-logo-text {
  font-size: 1.5rem;
  font-weight: 700;
  color: #18723F;
  text-align: center;
  letter-spacing: 0.5px;
  text-transform: uppercase;
  position: relative;
  z-index: 1;
}

.partner-logo-general .partner-logo-text {
  font-size: 2rem;
  background: linear-gradient(135deg, #18723F 0%, #6f42c1 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.partner-logo-img {
  max-width: 100%;
  max-height: 140px;
  width: auto;
  height: auto;
  object-fit: contain;
  display: block;
  transition: transform 0.3s ease;
}

.partner-logo-wrapper:hover .partner-logo-img {
  transform: scale(1.05);
}

/* Partner Sponsor Wrapper */
.partner-sponsor-wrapper {
  position: relative;
}

/* Partner Card Animation */
.partner-card-animated {
  animation: partnerCardFadeIn 0.8s cubic-bezier(0.25, 0.46, 0.45, 0.94);
  transition: all 0.6s cubic-bezier(0.25, 0.46, 0.45, 0.94);
}

@keyframes partnerCardFadeIn {
  from {
    opacity: 0.7;
    transform: scale(0.98) translateY(3px);
  }
  to {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}

/* Partner Info Tooltip */
.partner-info-tooltip {
  position: absolute;
  top: 50%;
  left: calc(100% + 20px);
  transform: translateY(-50%);
  width: 350px;
  max-width: calc(100vw - 40px);
  z-index: 1000;
  animation: tooltipFadeIn 0.3s ease-out;
  pointer-events: auto;
}

@keyframes tooltipFadeIn {
  from {
    opacity: 0;
    transform: translateY(-50%) translateX(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(-50%) translateX(0);
  }
}

.partner-info-content {
  background: #fff;
  border-radius: 16px;
  padding: 1.5rem;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
  border: 2px solid rgba(24, 114, 63, 0.2);
  position: relative;
}

.partner-info-arrow {
  position: absolute;
  left: -10px;
  top: 50%;
  transform: translateY(-50%);
  width: 0;
  height: 0;
  border-top: 10px solid transparent;
  border-bottom: 10px solid transparent;
  border-right: 10px solid #fff;
  filter: drop-shadow(-2px 0 2px rgba(0, 0, 0, 0.1));
}

.partner-info-title {
  font-size: 1.25rem;
  font-weight: 700;
  color: #18723F;
  margin-bottom: 0.75rem;
  padding-bottom: 0.75rem;
  border-bottom: 2px solid rgba(24, 114, 63, 0.1);
}

.partner-info-description {
  font-size: 0.9rem;
  color: #495057;
  line-height: 1.6;
  margin-bottom: 1rem;
}

.partner-info-services {
  margin-top: 1rem;
  padding-top: 1rem;
  border-top: 1px solid rgba(24, 114, 63, 0.1);
}

.partner-info-services-title {
  font-size: 0.85rem;
  font-weight: 600;
  color: #18723F;
  margin-bottom: 0.5rem;
}

.partner-info-services-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.partner-info-services-list li {
  font-size: 0.8rem;
  color: #6c757d;
  padding: 0.25rem 0;
  padding-left: 1.25rem;
  position: relative;
  line-height: 1.5;
}

.partner-info-services-list li::before {
  content: '✓';
  position: absolute;
  left: 0;
  color: #18723F;
  font-weight: bold;
  font-size: 0.9rem;
}

.partner-info-telegram {
  margin-top: 1rem;
  padding-top: 1rem;
  border-top: 1px solid rgba(24, 114, 63, 0.1);
}

.partner-telegram-link {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  color: #0088cc;
  text-decoration: none;
  font-size: 0.9rem;
  font-weight: 600;
  padding: 0.5rem 1rem;
  background: rgba(0, 136, 204, 0.1);
  border-radius: 8px;
  transition: all 0.3s ease;
}

.partner-telegram-link:hover {
  background: rgba(0, 136, 204, 0.2);
  color: #006ba3;
  transform: translateX(2px);
}

.partner-telegram-link i {
  font-size: 1.1rem;
}

/* Tooltip positioning for mobile */
@media (max-width: 992px) {
  .partner-info-tooltip {
    left: 50%;
    top: calc(100% + 15px);
    transform: translateX(-50%);
    width: calc(100vw - 2rem);
    max-width: 400px;
    position: fixed;
    z-index: 1001;
  }

  .partner-info-tooltip-mobile {
    position: absolute;
  }

  .partner-info-arrow {
    left: 50%;
    top: -10px;
    transform: translateX(-50%);
    border-right: 10px solid transparent;
    border-left: 10px solid transparent;
    border-top: none;
    border-bottom: 10px solid #fff;
    filter: drop-shadow(0 -2px 2px rgba(0, 0, 0, 0.1));
  }
}

.partner-badge-general,
.partner-badge-event {
  position: absolute;
  bottom: 1rem;
  left: 50%;
  transform: translateX(-50%);
  padding: 0.5rem 1rem;
  border-radius: 20px;
  font-size: 0.75rem;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  white-space: nowrap;
  z-index: 10;
  pointer-events: none;
}

.partner-badge-general {
  background: linear-gradient(135deg, #18723F 0%, #6f42c1 100%);
  color: #fff;
  box-shadow: 0 4px 12px rgba(24, 114, 63, 0.3);
  padding: 0.6rem 1.2rem;
  font-size: 0.8rem;
}

.partner-badge-event {
  background: rgba(24, 114, 63, 0.1);
  color: #18723F;
  border: 1px solid rgba(24, 114, 63, 0.2);
}

.partner-badge-general i,
.partner-badge-event i {
  font-size: 0.9rem;
}

/* Horizontal Scroll Container for Mobile */
.partners-scroll-container {
  overflow-x: auto;
  overflow-y: hidden;
  -webkit-overflow-scrolling: touch;
  scrollbar-width: thin;
  scrollbar-color: rgba(24, 114, 63, 0.3) transparent;
  padding-bottom: 1rem;
  margin: 0 -1rem;
  padding-left: 1rem;
  padding-right: 1rem;
}

.partners-scroll-container::-webkit-scrollbar {
  height: 6px;
}

.partners-scroll-container::-webkit-scrollbar-track {
  background: transparent;
  border-radius: 10px;
}

.partners-scroll-container::-webkit-scrollbar-thumb {
  background: rgba(24, 114, 63, 0.3);
  border-radius: 10px;
}

.partners-scroll-container::-webkit-scrollbar-thumb:hover {
  background: rgba(24, 114, 63, 0.5);
}

.partners-scroll-wrapper {
  display: flex;
  gap: 1rem;
  width: max-content;
}

@media (max-width: 576px) {
  .partners-scroll-wrapper {
    gap: 0.5rem;
  }
}

/* General Sponsors Scroll - one full + half visible */
.partners-sponsors-scroll {
  padding-right: calc(50vw - 1rem);
}

.partner-sponsor-mobile {
  flex: 0 0 auto;
  width: calc(85vw - 2rem);
  max-width: 320px;
  position: relative;
  margin-right: 1rem;
}

@media (max-width: 576px) {
  .partner-sponsor-mobile {
    margin-right: 0.5rem;
  }
}


/* Responsive Design for Partners */
@media (max-width: 768px) {
  .partner-card-general {
    padding: 2rem 1.5rem;
    min-height: 240px;
  }

  .partner-card-event {
    padding: 1.5rem 1rem;
    min-height: 160px;
  }

  .partner-logo-general {
    min-height: 150px;
    padding: 2rem 1.5rem;
  }

  .partner-logo-event {
    min-height: 100px;
    padding: 1.25rem;
  }

  .partner-logo-general .partner-logo-text {
    font-size: 1.5rem;
  }

  .partner-logo-text {
    font-size: 1.2rem;
  }

  .partner-badge-general {
    padding: 0.5rem 1rem;
    font-size: 0.75rem;
  }

  .partner-badge-event {
    padding: 0.4rem 0.8rem;
    font-size: 0.7rem;
  }
}

@media (max-width: 576px) {
  .partner-card {
    min-height: 160px;
  }

  .partner-card-general {
    padding: 1.25rem 0.6rem;
    min-height: 160px;
    padding-bottom: 3rem;
  }

  .partner-card-event {
    padding: 1.25rem 0.75rem;
    min-height: 140px;
  }

  .partner-logo-general {
    min-height: 90px;
    padding: 1.25rem 0.6rem;
  }

  .partner-logo-event {
    min-height: 80px;
    padding: 1rem;
  }

  .partner-logo-general .partner-logo-text {
    font-size: 1rem;
  }

  .partner-logo-text {
    font-size: 1rem;
  }

  .partner-badge-general,
  .partner-badge-event {
    bottom: 0.5rem;
    font-size: 0.65rem;
    padding: 0.4rem 0.8rem;
    white-space: nowrap;
    max-width: calc(100% - 1rem);
    overflow: visible;
  }
  
  .partner-badge-general {
    font-size: 0.7rem;
    padding: 0.45rem 0.9rem;
  }

  .partner-logo-img {
    max-height: 70px;
  }

  .partner-info-tooltip {
    width: calc(100vw - 2rem);
    max-width: 350px;
  }

  .partner-info-content {
    padding: 1.25rem;
  }

  .partner-info-title {
    font-size: 1.1rem;
  }

  .partner-info-description {
    font-size: 0.85rem;
  }

  /* Mobile scroll adjustments */
  .partner-sponsor-mobile {
    width: calc(80vw - 1.5rem);
    max-width: 280px;
  }


  .partners-sponsors-scroll {
    padding-right: calc(50vw - 1rem);
  }
}

/* Sponsor Modal Styles */
.sponsor-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  backdrop-filter: blur(4px);
  z-index: 2000;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 1rem;
  animation: modalFadeIn 0.3s ease-out;
}

@keyframes modalFadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.sponsor-modal {
  background: #fff;
  border-radius: 20px;
  max-width: 600px;
  width: 100%;
  max-height: 90vh;
  overflow-y: auto;
  position: relative;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  animation: modalSlideIn 0.3s ease-out;
}

@keyframes modalSlideIn {
  from {
    opacity: 0;
    transform: translateY(-20px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.sponsor-modal-close {
  position: absolute;
  top: 1rem;
  right: 1rem;
  width: 40px;
  height: 40px;
  border: none;
  background: rgba(0, 0, 0, 0.1);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  z-index: 10;
  transition: all 0.3s ease;
  color: #333;
  font-size: 1.2rem;
}

.sponsor-modal-close:hover {
  background: rgba(0, 0, 0, 0.2);
  transform: rotate(90deg);
}

.sponsor-modal-content {
  padding: 2rem;
}

.sponsor-modal-header {
  text-align: center;
  margin-bottom: 2rem;
  padding-top: 1rem;
}

.sponsor-modal-logo {
  margin-bottom: 1.5rem;
  display: flex;
  justify-content: center;
}

.sponsor-modal-logo img {
  max-width: 200px;
  max-height: 120px;
  object-fit: contain;
}

.sponsor-modal-title {
  font-size: 1.75rem;
  font-weight: 700;
  color: #18723F;
  margin-bottom: 1rem;
}

.sponsor-modal-badge {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 1rem;
  background: linear-gradient(135deg, #18723F 0%, #6f42c1 100%);
  color: #fff;
  border-radius: 20px;
  font-size: 0.85rem;
  font-weight: 600;
}

.sponsor-modal-description {
  margin-bottom: 2rem;
  padding: 1.5rem;
  background: rgba(24, 114, 63, 0.05);
  border-radius: 12px;
  border-left: 4px solid #18723F;
}

.sponsor-modal-description p {
  margin: 0;
  line-height: 1.7;
  color: #495057;
  font-size: 1rem;
}

.sponsor-modal-services {
  margin-bottom: 2rem;
}

.sponsor-modal-services-title {
  font-size: 1.1rem;
  font-weight: 600;
  color: #18723F;
  margin-bottom: 1rem;
}

.sponsor-modal-services-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.sponsor-modal-services-list li {
  display: flex;
  align-items: flex-start;
  gap: 0.75rem;
  padding: 0.75rem;
  margin-bottom: 0.5rem;
  background: #fff;
  border-radius: 8px;
  border: 1px solid rgba(24, 114, 63, 0.1);
  transition: all 0.3s ease;
}

.sponsor-modal-services-list li:hover {
  background: rgba(24, 114, 63, 0.05);
  border-color: rgba(24, 114, 63, 0.2);
  transform: translateX(5px);
}

.sponsor-modal-services-list li i {
  color: #18723F;
  font-size: 1.1rem;
  margin-top: 0.2rem;
  flex-shrink: 0;
}

.sponsor-modal-telegram {
  text-align: center;
  padding-top: 1.5rem;
  border-top: 2px solid rgba(24, 114, 63, 0.1);
}

.sponsor-telegram-link {
  display: inline-flex;
  align-items: center;
  gap: 0.75rem;
  color: #0088cc;
  text-decoration: none;
  font-size: 1rem;
  font-weight: 600;
  padding: 0.75rem 1.5rem;
  background: rgba(0, 136, 204, 0.1);
  border-radius: 12px;
  transition: all 0.3s ease;
}

.sponsor-telegram-link:hover {
  background: rgba(0, 136, 204, 0.2);
  color: #006ba3;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 136, 204, 0.2);
}

.sponsor-telegram-link i {
  font-size: 1.3rem;
}

/* Mobile styles for modal */
@media (max-width: 576px) {
  .sponsor-modal-overlay {
    padding: 0.5rem;
  }

  .sponsor-modal {
    max-height: 95vh;
    border-radius: 16px;
  }

  .sponsor-modal-close {
    top: 0.75rem;
    right: 0.75rem;
    width: 36px;
    height: 36px;
    font-size: 1rem;
  }

  .sponsor-modal-content {
    padding: 1.5rem 1rem;
  }

  .sponsor-modal-header {
    margin-bottom: 1.5rem;
  }

  .sponsor-modal-logo img {
    max-width: 150px;
    max-height: 100px;
  }

  .sponsor-modal-title {
    font-size: 1.4rem;
    margin-bottom: 0.75rem;
  }

  .sponsor-modal-badge {
    font-size: 0.75rem;
    padding: 0.4rem 0.8rem;
  }

  .sponsor-modal-description {
    padding: 1rem;
    margin-bottom: 1.5rem;
  }

  .sponsor-modal-description p {
    font-size: 0.9rem;
  }

  .sponsor-modal-services-title {
    font-size: 1rem;
    margin-bottom: 0.75rem;
  }

  .sponsor-modal-services-list li {
    padding: 0.6rem;
    font-size: 0.9rem;
  }

  .sponsor-telegram-link {
    font-size: 0.9rem;
    padding: 0.6rem 1.2rem;
  }
}
</style>

