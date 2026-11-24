const API = '/api';
const roomPhotos = {
  Standard: 'https://images.unsplash.com/photo-1611892440504-42a792e24d32?auto=format&fit=crop&w=900&q=80',
  Deluxe: 'https://images.unsplash.com/photo-1618221195710-dd6b41faaea6?auto=format&fit=crop&w=900&q=80',
  Suite: 'https://images.unsplash.com/photo-1616486338812-3dadae4b4ace?auto=format&fit=crop&w=900&q=80'
};
const today = new Date();
const dateString = date => `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
const tomorrow = new Date(today); tomorrow.setDate(today.getDate() + 1);
const dayAfter = new Date(today); dayAfter.setDate(today.getDate() + 2);
const checkInInput = document.querySelector('#check-in');
const checkOutInput = document.querySelector('#check-out');
checkInInput.min = dateString(today); checkInInput.value = dateString(tomorrow);
checkOutInput.min = dateString(tomorrow); checkOutInput.value = dateString(dayAfter);
checkInInput.addEventListener('change', () => {
  const next = new Date(`${checkInInput.value}T00:00:00`); next.setDate(next.getDate() + 1);
  checkOutInput.min = dateString(next);
  if (checkOutInput.value <= checkInInput.value) checkOutInput.value = dateString(next);
});

const statusEl = document.querySelector('#status');
const roomGrid = document.querySelector('#room-grid');
const toast = document.querySelector('#toast');
let selectedRoom = null;
let toastTimer;

async function request(url, options = {}) {
  const response = await fetch(url, { headers: { 'Content-Type': 'application/json' }, ...options });
  if (!response.ok) {
    let message = `Request failed (${response.status})`;
    try { const body = await response.json(); message = body.detail || body.message || body.error || message; } catch { /* use fallback */ }
    throw new Error(message);
  }
  if (response.status === 204) return null;
  return response.json();
}
function showToast(message) {
  toast.textContent = message; toast.classList.add('show'); clearTimeout(toastTimer);
  toastTimer = setTimeout(() => toast.classList.remove('show'), 3500);
}
function money(value) { return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD', maximumFractionDigits: 0 }).format(value); }

async function searchRooms(event) {
  if (event) event.preventDefault();
  const checkIn = checkInInput.value, checkOut = checkOutInput.value;
  if (!checkIn || !checkOut || checkOut <= checkIn) { statusEl.textContent = 'Please choose a check-out date after check-in.'; return; }
  statusEl.textContent = 'Finding your room…'; roomGrid.innerHTML = '';
  const category = document.querySelector('#category').value;
  const params = new URLSearchParams({ checkIn, checkOut });
  if (category) params.set('category', category);
  try {
    const rooms = await request(`${API}/rooms?${params}`);
    const available = rooms.filter(room => room.available);
    statusEl.textContent = available.length ? `${available.length} room${available.length === 1 ? '' : 's'} available for your dates` : 'No rooms available for these dates. Try different dates or room style.';
    roomGrid.innerHTML = available.map(room => `
      <article class="room-card">
        <div class="room-photo" style="background-image:url('${roomPhotos[room.category] || roomPhotos.Standard}')"><span class="room-tag">${escapeHtml(room.category.toUpperCase())}</span></div>
        <div class="room-info"><h3>${escapeHtml(room.category)} Room</h3><p class="room-details">Room ${room.roomNumber} &nbsp;·&nbsp; Forest view &nbsp;·&nbsp; 2 guests</p>
          <div class="room-bottom"><span class="price">${money(room.pricePerNight)} <small>/ night</small></span><button type="button" data-room="${room.roomNumber}" data-category="${escapeHtml(room.category)}" data-price="${room.pricePerNight}">Reserve room →</button></div>
        </div>
      </article>`).join('');
    roomGrid.querySelectorAll('button[data-room]').forEach(button => button.addEventListener('click', () => openReservation(button)));
  } catch (error) { statusEl.textContent = error.message; }
}
function escapeHtml(value) {
  return String(value).replace(/[&<>"']/g, character => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' })[character]);
}
function openReservation(button) {
  selectedRoom = { roomNumber: Number(button.dataset.room), category: button.dataset.category, price: Number(button.dataset.price) };
  document.querySelector('#selected-room').textContent = `${selectedRoom.category} · Room ${selectedRoom.roomNumber} · ${money(selectedRoom.price)} per night`;
  document.querySelector('#dialog-check-in').value = checkInInput.value;
  document.querySelector('#dialog-check-out').value = checkOutInput.value;
  document.querySelector('#dialog-check-in').min = dateString(today);
  document.querySelector('#dialog-check-out').min = checkInInput.value;
  document.querySelector('#dialog-message').textContent = '';
  document.querySelector('#reservation-dialog').showModal();
}

document.querySelector('#search-form').addEventListener('submit', searchRooms);
document.querySelector('#reservation-form').addEventListener('submit', async event => {
  event.preventDefault();
  if (!selectedRoom) return;
  const message = document.querySelector('#dialog-message');
  const checkInDate = document.querySelector('#dialog-check-in').value;
  const checkOutDate = document.querySelector('#dialog-check-out').value;
  if (!checkInDate || !checkOutDate || checkOutDate <= checkInDate) { message.textContent = 'Check-out must be after check-in.'; return; }
  try {
    await request(`${API}/reservations`, { method: 'POST', body: JSON.stringify({
      guestName: document.querySelector('#guest-name').value.trim(), roomNumber: selectedRoom.roomNumber, checkInDate, checkOutDate
    }) });
    document.querySelector('#reservation-dialog').close();
    document.querySelector('#reservation-form').reset();
    showToast('Reservation confirmed. We look forward to welcoming you!');
    await Promise.all([searchRooms(), loadReservations()]);
    location.hash = 'reservations';
  } catch (error) { message.textContent = error.message; }
});

async function loadReservations() {
  const container = document.querySelector('#reservation-list');
  container.innerHTML = '<div class="empty-state">Loading reservations…</div>';
  try {
    const reservations = await request(`${API}/reservations`);
    if (!reservations.length) { container.innerHTML = '<div class="empty-state">No reservations yet. Your next getaway could start here.</div>'; return; }
    container.innerHTML = reservations.map(reservation => `
      <article class="reservation-card"><strong>${escapeHtml(reservation.guestName)}</strong>
        <span>${escapeHtml(reservation.category)} · Room ${reservation.roomNumber}</span>
        <span>${reservation.checkInDate} → ${reservation.checkOutDate} · ${money(reservation.totalAmount)}</span>
        <button type="button" data-cancel="${reservation.id}">Cancel reservation</button>
      </article>`).join('');
    container.querySelectorAll('[data-cancel]').forEach(button => button.addEventListener('click', async () => {
      if (!confirm('Cancel this reservation?')) return;
      try { await request(`${API}/reservations/${button.dataset.cancel}`, { method: 'DELETE' }); showToast('Reservation cancelled.'); await Promise.all([loadReservations(), searchRooms()]); }
      catch (error) { showToast(error.message); }
    }));
  } catch (error) { container.innerHTML = `<div class="empty-state">${escapeHtml(error.message)}. Make sure the app and MySQL database are running.</div>`; }
}
document.querySelector('#refresh-reservations').addEventListener('click', loadReservations);
searchRooms();
loadReservations();
