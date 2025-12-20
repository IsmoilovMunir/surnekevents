<template>
  <div v-if="open" class="modal-backdrop-custom" @click.self="close">
    <div class="modal-panel p-4">
      <div class="d-flex justify-content-between align-items-center mb-3">
        <div>
          <h2 class="h4 mb-1">Выбор мест</h2>
          <p class="text-body-secondary mb-0">Выберите до 10 мест на схеме зала.</p>
        </div>
        <button class="btn btn-outline-secondary" @click="close">
          <i class="bi bi-x-lg me-1" /> Закрыть
        </button>
      </div>
      <div class="row g-4 flex-grow-1 seat-map-layout">
        <div class="col-lg-8 seat-map-column">
          <div class="price-filter btn-group mb-3 flex-nowrap flex-shrink-0">
            <button
              v-for="option in priceOptions"
              :key="option.label"
              type="button"
              class="btn"
              :class="priceFilter === option.value ? 'btn-primary' : 'btn-outline-primary'"
              @click="selectPriceFilter(option.value)"
            >
              {{ option.label }}
            </button>
          </div>
          <div class="seat-map border rounded-4 p-3 bg-body flex-grow-1 d-flex flex-column position-relative">
            <HallMap class="flex-grow-1" />
            <!-- Кнопка "Купить X билет" внизу карты для мобильных -->
            <div class="d-lg-none buy-button-toolbar">
              <button
                class="buy-btn-toolbar"
                :disabled="selectedSeats.length === 0"
                @click="showCheckoutPanel = true"
              >
                <span v-if="selectedSeats.length > 0">
                  Купить {{ getTicketText(selectedSeats.length) }}
                </span>
                <span v-else>Выберите места</span>
              </button>
            </div>
          </div>
          <LegendPanel class="mt-3 flex-shrink-0" />
        </div>
        <div class="col-lg-4 cart-column">
          <div class="cart-panel border rounded-4 p-3 bg-light-subtle h-100 d-flex flex-column">
            <h3 class="h5 mb-2">Корзина</h3>
            <p class="text-body-secondary small mb-2" style="font-size: 0.75rem;">
              Выбранные места будут удерживаться 30 минут после отправки заявки.
            </p>
            <div class="checkout-summary sticky-checkout mb-2">
              <div class="d-flex justify-content-between align-items-center mb-2">
                <span class="fw-semibold small">Сумма: {{ formatPrice(calculatedTotal) }}</span>
                <small class="text-body-secondary" style="font-size: 0.75rem;">{{ selected.size }}/10 мест</small>
              </div>
              <div v-if="discountAmount > 0" class="text-success small mb-2">
                Скидка: -{{ formatPrice(discountAmount) }}
              </div>
              <div class="form-check mb-2">
                <input
                  class="form-check-input"
                  type="checkbox"
                  id="consentCheckbox"
                  v-model="consentToProcessing"
                  style="margin-top: 0.2rem;"
                />
                <label class="form-check-label small" for="consentCheckbox" style="font-size: 0.75rem; line-height: 1.3;">
                  Я согласен на обработку персональных данных
                </label>
              </div>
              <button
                class="btn btn-primary w-100 btn-sm"
                :disabled="selectedSeats.length === 0 || reservationLoading || !consentToProcessing"
                @click="submitReservation"
              >
                <span v-if="reservationLoading" class="spinner-border spinner-border-sm me-2"></span>
                Отправить менеджеру
              </button>
              <p v-if="successMessage" class="text-success small mt-2 mb-0" style="font-size: 0.75rem;">
                {{ successMessage }}
              </p>
              <p v-else class="text-body-secondary small mt-2 mb-0" style="font-size: 0.7rem;">
                После отправки менеджер свяжется с вами, чтобы подтвердить заказ.
              </p>
            </div>
            <div class="contact-form-block mb-2">
              <label class="form-label small text-uppercase text-body-secondary mb-1">Имя</label>
              <input
                v-model="reservationStore.contact.name"
                class="form-control form-control-sm mb-1"
                placeholder="Имя и фамилия"
              />
              <label class="form-label small text-uppercase text-body-secondary mb-1">Телефон</label>
              <input
                v-model="reservationStore.contact.phone"
                class="form-control form-control-sm mb-1"
                placeholder="+7"
              />
              <label class="form-label small text-uppercase text-body-secondary mb-1">
                Email (билет отправим на вашу почту)
              </label>
              <input
                v-model="reservationStore.contact.email"
                class="form-control form-control-sm mb-1"
                placeholder="example@site.com"
              />
              <small class="text-body-secondary small">Укажите почту, чтобы получить билеты на email.</small>
            </div>
            <div class="promo-code-block mb-2">
              <label class="form-label small text-uppercase text-body-secondary mb-1">Промокод</label>
              <div class="input-group input-group-sm">
                <input
                  v-model="reservationStore.promoCode"
                  class="form-control"
                  placeholder="Введите промокод"
                  @blur="validatePromoCode"
                />
                <button
                  class="btn btn-outline-secondary"
                  type="button"
                  @click="validatePromoCode"
                  :disabled="!reservationStore.promoCode.trim()"
                >
                  Применить
                </button>
              </div>
              <p v-if="reservationStore.promoCodeError" class="text-danger small mt-1 mb-0" style="font-size: 0.7rem;">
                {{ reservationStore.promoCodeError }}
              </p>
              <p v-else-if="reservationStore.promoCodeDiscount" class="text-success small mt-1 mb-0" style="font-size: 0.7rem;">
                Скидка {{ reservationStore.promoCodeDiscount }}% применена
              </p>
            </div>
            <div
              class="selected-seats-container flex-grow-1"
              :class="{ 'selected-seats-scroll': selectedSeats.length > 2 }"
            >
              <div v-if="selectedSeats.length === 0" class="text-center text-body-secondary py-3">
                Выберите места на схеме
              </div>
              <div v-else class="list-group list-group-flush">
                <div v-for="seat in selectedSeats" :key="seat.id" class="list-group-item border-0 px-0 py-2 d-flex justify-content-between align-items-center">
                  <div class="flex-grow-1">
                    <div class="fw-semibold small">
                      Стол {{ seat.tableNumber }}, Место {{ seat.chairNumber }}
                    </div>
                    <div class="text-body-secondary" style="font-size: 0.75rem;">{{ getCategoryDisplayName(seat.categoryName) }}</div>
                  </div>
                  <div class="fw-semibold small ms-2">
                    {{ formatPrice(getSeatPrice(seat)) }}
                    <span v-if="getSeatDiscount(seat) > 0" class="text-success" style="font-size: 0.7rem;">
                      (скидка -{{ formatPrice(getSeatDiscount(seat)) }})
                    </span>
                  </div>
                </div>
              </div>
            </div>
            <p v-if="reservationError" class="text-danger small mt-2">{{ reservationError }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- Выдвижная панель справа для мобильных -->
    <div
      v-if="showCheckoutPanel"
      class="checkout-sidebar-wrapper"
    >
      <div class="checkout-sidebar-backdrop" @click="showCheckoutPanel = false"></div>
      <div class="checkout-sidebar">
        <div class="checkout-sidebar-content">
        <div class="checkout-sidebar-header d-flex justify-content-between align-items-center mb-3">
          <h3 class="h5 mb-0">Оформление заказа</h3>
          <button class="btn btn-sm btn-outline-secondary" @click="showCheckoutPanel = false">
            <i class="bi bi-x-lg"></i>
          </button>
        </div>

        <!-- Информация о билетах -->
        <div class="mb-3">
          <h4 class="h6 mb-2">Выбранные места</h4>
          <div class="list-group list-group-flush">
            <div
              v-for="seat in selectedSeats"
              :key="seat.id"
              class="list-group-item border-0 px-0 py-2 d-flex justify-content-between align-items-center"
            >
              <div class="flex-grow-1">
                <div class="fw-semibold small">
                  Стол {{ seat.tableNumber }}, Место {{ seat.chairNumber }}
                </div>
                <div class="text-body-secondary" style="font-size: 0.75rem;">
                  {{ seat.categoryName }}
                </div>
              </div>
              <div class="fw-semibold small ms-2">
                {{ formatPrice(getSeatPrice(seat)) }}
                <span v-if="getSeatDiscount(seat) > 0" class="text-success" style="font-size: 0.7rem;">
                  (скидка -{{ formatPrice(getSeatDiscount(seat)) }})
                </span>
              </div>
            </div>
          </div>
          <div class="mt-3 p-2 bg-light rounded">
            <div class="d-flex justify-content-between align-items-center">
              <span class="fw-semibold">Итого:</span>
              <span class="fw-bold text-primary fs-5">{{ formatPrice(calculatedTotal) }}</span>
            </div>
            <div v-if="discountAmount > 0" class="text-success small mt-1">
              Скидка: -{{ formatPrice(discountAmount) }}
            </div>
            <div class="text-body-secondary small mt-1">
              {{ selectedSeats.length }} {{ getTicketText(selectedSeats.length) }}
            </div>
          </div>
        </div>

        <!-- Форма контактов -->
        <div class="contact-form-block mb-3">
          <h4 class="h6 mb-2">Контактная информация</h4>
          <label class="form-label small text-uppercase text-body-secondary mb-1">Имя</label>
          <input
            v-model="reservationStore.contact.name"
            class="form-control form-control-sm mb-2"
            placeholder="Имя и фамилия"
          />
          <label class="form-label small text-uppercase text-body-secondary mb-1">Телефон</label>
          <input
            v-model="reservationStore.contact.phone"
            class="form-control form-control-sm mb-2"
            placeholder="+7"
          />
          <label class="form-label small text-uppercase text-body-secondary mb-1">
            Email (билет отправим на вашу почту)
          </label>
          <input
            v-model="reservationStore.contact.email"
            class="form-control form-control-sm mb-2"
            placeholder="example@site.com"
          />
          <small class="text-body-secondary">Укажите почту, чтобы получить билеты на email.</small>
        </div>

        <!-- Промокод -->
        <div class="promo-code-block mb-3">
          <h4 class="h6 mb-2">Промокод</h4>
          <div class="input-group input-group-sm">
            <input
              v-model="reservationStore.promoCode"
              class="form-control"
              placeholder="Введите промокод"
              @blur="validatePromoCode"
            />
            <button
              class="btn btn-outline-secondary"
              type="button"
              @click="validatePromoCode"
              :disabled="!reservationStore.promoCode.trim()"
            >
              Применить
            </button>
          </div>
          <p v-if="reservationStore.promoCodeError" class="text-danger small mt-1 mb-0">
            {{ reservationStore.promoCodeError }}
          </p>
          <p v-else-if="reservationStore.promoCodeDiscount" class="text-success small mt-1 mb-0">
            Скидка {{ reservationStore.promoCodeDiscount }}% применена
          </p>
        </div>

        <!-- Согласие и кнопка отправки -->
        <div class="checkout-actions">
          <div class="form-check mb-3">
            <input
              class="form-check-input"
              type="checkbox"
              id="consentCheckboxSidebar"
              v-model="consentToProcessing"
            />
            <label class="form-check-label small" for="consentCheckboxSidebar" style="font-size: 0.75rem; line-height: 1.3;">
              Я согласен на обработку персональных данных
            </label>
          </div>
          <button
            class="btn btn-primary w-100 btn-lg"
            :disabled="selectedSeats.length === 0 || reservationLoading || !consentToProcessing"
            @click="submitReservation"
          >
            <span v-if="reservationLoading" class="spinner-border spinner-border-sm me-2"></span>
            Отправить менеджеру
          </button>
          <p v-if="successMessage" class="text-success small mt-2 mb-0">
            {{ successMessage }}
          </p>
          <p v-else-if="reservationError" class="text-danger small mt-2 mb-0">
            {{ reservationError }}
          </p>
          <p v-else class="text-body-secondary small mt-2 mb-0">
            После отправки менеджер свяжется с вами, чтобы подтвердить заказ.
          </p>
        </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, watch, ref } from 'vue';
import HallMap from './HallMap.vue';
import LegendPanel from './LegendPanel.vue';
import { useSeatStore } from '../stores/seatStore';
import { useReservationStore } from '../stores/reservationStore';
import { validatePromoCode as validatePromoCodeApi } from '../services/api';
import type { Seat } from '../types';

const props = defineProps<{
  open: boolean;
  concertId: number;
}>();

const emit = defineEmits(['close']);

const seatStore = useSeatStore();
const reservationStore = useReservationStore();
const consentToProcessing = ref(false);
const showCheckoutPanel = ref(false);

// Функция форматирования цены
const formatPrice = (cents: number) =>
  new Intl.NumberFormat('ru-RU', {
    style: 'currency',
    currency: 'RUB'
  }).format(cents / 100);

// Динамически формируем опции фильтров по ценам из загруженных мест
const priceOptions = computed(() => {
  const options: Array<{ label: string; value: number | null }> = [
    { label: 'Все категории', value: null }
  ];
  
  // Извлекаем уникальные цены из мест
  const uniquePrices = new Set<number>();
  seatStore.seats.forEach((seat) => {
    if (seat.priceCents) {
      uniquePrices.add(seat.priceCents);
    }
  });
  
  // Сортируем цены по убыванию и создаем опции
  const sortedPrices = Array.from(uniquePrices).sort((a, b) => b - a);
  sortedPrices.forEach((priceCents) => {
    options.push({
      label: formatPrice(priceCents),
      value: priceCents
    });
  });
  
  return options;
});

const selectedSeats = computed(() => seatStore.selectedSeats);
const selected = computed(() => seatStore.selected);
const total = computed(() => seatStore.totalSelectedPrice);
const reservationLoading = computed(() => reservationStore.loading);
const reservationError = computed(() => reservationStore.error);
const successMessage = computed(() => reservationStore.successMessage);
const priceFilter = computed(() => seatStore.priceFilter);

const discountAmount = computed(() => {
  if (!reservationStore.promoCodeDiscount || selectedSeats.value.length === 0) {
    return 0;
  }
  // Применяем скидку только к билетам, к которым применим промокод
  // В реальности это будет проверено на бэкенде, но для отображения применяем ко всем
  return Math.floor(total.value * reservationStore.promoCodeDiscount / 100);
});

const calculatedTotal = computed(() => {
  return total.value - discountAmount.value;
});

const validatePromoCodeHandler = async () => {
  if (!reservationStore.promoCode.trim()) {
    reservationStore.promoCodeError = 'Введите промокод';
    reservationStore.promoCodeDiscount = null;
    return;
  }

  if (selectedSeats.value.length === 0) {
    reservationStore.promoCodeError = 'Выберите места для проверки промокода';
    reservationStore.promoCodeDiscount = null;
    return;
  }

  const categoryIds = selectedSeats.value.map(seat => seat.categoryId);
  try {
    const response = await validatePromoCodeApi({
      code: reservationStore.promoCode.trim(),
      seatCategoryIds: categoryIds
    });
    
    if (response.valid && response.discountPercent) {
      reservationStore.promoCodeDiscount = response.discountPercent;
      reservationStore.promoCodeError = null;
    } else {
      reservationStore.promoCodeDiscount = null;
      reservationStore.promoCodeError = response.message || 'Промокод недействителен';
    }
  } catch (error: any) {
    reservationStore.promoCodeDiscount = null;
    reservationStore.promoCodeError = error.response?.data?.message || 'Ошибка при проверке промокода';
    console.error('Promo code validation error:', error);
  }
};

// Алиас для использования в шаблоне
const validatePromoCode = validatePromoCodeHandler;

// Получаем цену билета с учетом скидки
const getSeatPrice = (seat: Seat) => {
  const discount = getSeatDiscount(seat);
  return seat.priceCents - discount;
};

// Получаем размер скидки для конкретного билета
const getSeatDiscount = (seat: Seat) => {
  if (!reservationStore.promoCodeDiscount) {
    return 0;
  }
  // Проверяем, применим ли промокод к этой категории
  // В реальности это проверяется на бэкенде, но для отображения применяем ко всем
  // если промокод был успешно применен
  return Math.floor(seat.priceCents * reservationStore.promoCodeDiscount / 100);
};

onMounted(() => {
  if (props.open) {
    seatStore.init(props.concertId);
  }
});

watch(
  () => props.open,
  (open) => {
    if (open) {
      seatStore.init(props.concertId);
      consentToProcessing.value = false; // Сбрасываем согласие при открытии модального окна
      showCheckoutPanel.value = false; // Закрываем панель при открытии модального окна
    }
  }
);

watch(
  () => selectedSeats.value.length,
  (newLength) => {
    if (newLength === 0) {
      showCheckoutPanel.value = false; // Закрываем панель, если все места сняты
      reservationStore.promoCodeError = null;
      reservationStore.promoCodeDiscount = null;
    } else if (reservationStore.promoCode.trim()) {
      // Перепроверяем промокод при изменении выбранных мест
      validatePromoCodeHandler();
    }
  }
);

watch(
  () => reservationStore.promoCode,
  () => {
    if (!reservationStore.promoCode.trim()) {
      reservationStore.promoCodeError = null;
      reservationStore.promoCodeDiscount = null;
    }
  }
);

const selectPriceFilter = (value: number | null) => {
  if (seatStore.priceFilter === value) {
    seatStore.setPriceFilter(null);
  } else {
    seatStore.setPriceFilter(value);
  }
};

const submitReservation = async () => {
  if (!consentToProcessing.value) {
    reservationStore.error = 'Необходимо дать согласие на обработку персональных данных';
    return;
  }
  await reservationStore.submitReservation(props.concertId, consentToProcessing.value);
  if (reservationStore.successMessage) {
    // Закрываем панель после успешной отправки
    setTimeout(() => {
      showCheckoutPanel.value = false;
    }, 2000);
  }
};

const close = () => emit('close');

const getCategoryDisplayName = (categoryName: string) => {
  // Убираем цену из названия категории (удаляем все цифры, пробелы и символ ₽)
  return categoryName.replace(/\s*\d+[\s₽]*/g, '').trim() || categoryName;
};

const getTicketText = (count: number): string => {
  const lastDigit = count % 10;
  const lastTwoDigits = count % 100;
  
  if (lastTwoDigits >= 11 && lastTwoDigits <= 19) {
    return `${count} билетов`;
  }
  
  if (lastDigit === 1) {
    return `${count} билет`;
  } else if (lastDigit >= 2 && lastDigit <= 4) {
    return `${count} билета`;
  } else {
    return `${count} билетов`;
  }
};
</script>

<style scoped>
/* Выдвижная панель справа для мобильных */
.checkout-sidebar-wrapper {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 9999;
  pointer-events: all;
}

.checkout-sidebar-backdrop {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  animation: fadeIn 0.3s ease;
}

.checkout-sidebar {
  position: absolute;
  top: 0;
  right: 0;
  width: 100%;
  max-width: 400px;
  height: 100%;
  background: white;
  box-shadow: -2px 0 10px rgba(0, 0, 0, 0.1);
  animation: slideInRight 0.3s ease;
  overflow-y: auto;
}

.checkout-sidebar-content {
  padding: 1.5rem;
  display: flex;
  flex-direction: column;
  height: 100%;
}

.checkout-sidebar-header {
  flex-shrink: 0;
  border-bottom: 1px solid #dee2e6;
  padding-bottom: 1rem;
}

.contact-form-block {
  flex-shrink: 0;
}

.checkout-actions {
  flex-shrink: 0;
  margin-top: auto;
  padding-top: 1rem;
  border-top: 1px solid #dee2e6;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes slideInRight {
  from {
    transform: translateX(100%);
  }
  to {
    transform: translateX(0);
  }
}

/* На десктопе панель не показывается */
@media (min-width: 992px) {
  .checkout-sidebar-wrapper {
    display: none;
  }
}
</style>

