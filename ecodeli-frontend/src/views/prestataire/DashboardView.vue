<script>
import apiServices from '@/services/apiServices';

export default {
  name: 'DashboardView',
  data() {
    return {
      prestataire: {
        nomEntreprise: '',
        tarifHoraire: 0,
        evaluations: []
      },
      disponible: false,
      prestationsCompletees: 0
    }
  },
  computed: {
    averageRating() {
      if (!this.prestataire.evaluations.length) return 0;
      const sum = this.prestataire.evaluations.reduce((acc, evaluation) => acc + evaluation.note, 0);
      return (sum / this.prestataire.evaluations.length).toFixed(1);
    },
    recentEvaluations() {
      return this.prestataire.evaluations
        .sort((a, b) => new Date(b.date) - new Date(a.date))
        .slice(0, 3);
    }
  },
  methods: {
    async loadPrestataireData() {
      try {
        const response = await apiServices.get('prestataires/profile');
        const data = await response.json();
        this.prestataire = data;
        this.disponible = data.disponible;
      } catch (error) {
        console.error('Erreur lors du chargement des données du prestataire:', error);
      }
    },
    async updateDisponibilite() {
      try {
        const response = await apiServices.put('prestataires/disponibilite', { disponible: this.disponible });
        if (!response.ok) {
          throw new Error('Erreur lors de la mise à jour de la disponibilité');
        }
      } catch (error) {
        this.disponible = !this.disponible;
      }
    },
    formatDate(date) {
      return new Date(date).toLocaleDateString('fr-FR', {
        day: 'numeric',
        month: 'long',
        year: 'numeric'
      });
    }
  },
  created() {
    this.loadPrestataireData();
  }
}
</script>

<template>
  <div class="dashboard-container">
    <div class="dashboard-header">
      <h1>Tableau de bord</h1>
      <div class="header-info">
        <h2>{{ prestataire.nomEntreprise }}</h2>
        <div class="rating">
          <i class="fas fa-star"></i>
          <span>{{ averageRating }} ({{ prestataire.evaluations.length }} avis)</span>
        </div>
      </div>
    </div>

    <div class="dashboard-grid">
      <!-- Statistiques -->
      <div class="dashboard-card stats">
        <h3>Statistiques</h3>
        <div class="stats-grid">
          <div class="stat-item">
            <i class="fas fa-clipboard-check"></i>
            <span class="stat-value">{{ prestationsCompletees }}</span>
            <span class="stat-label">Prestations complétées</span>
          </div>
          <div class="stat-item">
            <i class="fas fa-star"></i>
            <span class="stat-value">{{ averageRating }}</span>
            <span class="stat-label">Note moyenne</span>
          </div>
          <div class="stat-item">
            <i class="fas fa-euro-sign"></i>
            <span class="stat-value">{{ prestataire.tarifHoraire }}€/h</span>
            <span class="stat-label">Tarif actuel</span>
          </div>
        </div>
      </div>

      <!-- Disponibilités -->
      <div class="dashboard-card availability">
        <h3>Disponibilité</h3>
        <div class="availability-toggle">
          <label class="switch">
            <input type="checkbox" v-model="disponible" @change="updateDisponibilite">
            <span class="slider"></span>
          </label>
          <span>{{ disponible ? 'Disponible' : 'Non disponible' }}</span>
        </div>
      </div>

      <!-- Avis récents -->
      <div class="dashboard-card reviews">
        <h3>Avis récents</h3>
        <div v-if="prestataire.evaluations.length" class="reviews-list">
          <div v-for="evaluation in recentEvaluations" :key="evaluation.idEvaluation" class="review-item">
            <div class="review-header">
              <div class="stars">
                <i v-for="n in 5" :key="n" 
                   class="fas fa-star"
                   :class="{ 'filled': n <= evaluation.note }">
                </i>
              </div>
              <span class="review-date">{{ formatDate(evaluation.date) }}</span>
            </div>
            <p class="review-text">{{ evaluation.commentaire }}</p>
          </div>
        </div>
        <p v-else class="no-reviews">Aucun avis pour le moment</p>
      </div>
    </div>
  </div>
</template>

<style scoped>
.dashboard-container {
  max-width: 1200px;
  margin: 2rem auto;
  padding: 0 1rem;
}

.dashboard-header {
  background-color: var(--card-bg);
  border-radius: 8px;
  padding: 2rem;
  margin-bottom: 2rem;
  box-shadow: var(--shadow);
  transition: background-color 0.3s ease, box-shadow 0.3s ease;
}

.dashboard-header h1 {
  margin: 0 0 1rem 0;
  color: var(--text-primary);
}

.header-info h2 {
  margin: 0;
  color: var(--text-primary);
  font-size: 1.5rem;
}

.rating {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: #ffc107;
  margin: 0.5rem 0;
}

.rating span {
  color: var(--text-secondary);
}

.dashboard-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 1.5rem;
}

.dashboard-card {
  background-color: var(--card-bg);
  border-radius: 8px;
  padding: 1.5rem;
  box-shadow: var(--shadow);
  transition: background-color 0.3s ease, box-shadow 0.3s ease;
}

.dashboard-card h3 {
  margin: 0 0 1.5rem 0;
  color: var(--text-primary);
  border-bottom: 1px solid var(--border-color);
  padding-bottom: 0.5rem;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(100px, 1fr));
  gap: 1rem;
}

.stat-item {
  text-align: center;
}

.stat-item i {
  font-size: 1.5rem;
  color: var(--primary-color);
  margin-bottom: 0.5rem;
}

.stat-value {
  display: block;
  font-size: 1.5rem;
  font-weight: bold;
  color: var(--text-primary);
}

.stat-label {
  display: block;
  font-size: 0.875rem;
  color: var(--text-secondary);
}

.availability-toggle {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.availability-toggle span {
  color: var(--text-primary);
}

.switch {
  position: relative;
  display: inline-block;
  width: 60px;
  height: 34px;
}

.switch input {
  opacity: 0;
  width: 0;
  height: 0;
}

.slider {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: var(--border-color);
  transition: .4s;
  border-radius: 34px;
}

.slider:before {
  position: absolute;
  content: "";
  height: 26px;
  width: 26px;
  left: 4px;
  bottom: 4px;
  background-color: white;
  transition: .4s;
  border-radius: 50%;
}

input:checked + .slider {
  background-color: var(--primary-color);
}

input:checked + .slider:before {
  transform: translateX(26px);
}

.reviews-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.review-item {
  padding: 1rem;
  border: 1px solid var(--border-color);
  border-radius: 4px;
  background-color: var(--input-bg);
  transition: background-color 0.3s ease, border-color 0.3s ease;
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.5rem;
}

.stars {
  color: #ffc107;
}

.stars i.filled {
  color: #ffc107;
}

.stars i:not(.filled) {
  color: var(--text-disabled);
}

.review-date {
  font-size: 0.875rem;
  color: var(--text-secondary);
}

.review-text {
  margin: 0;
  color: var(--text-primary);
}

.no-reviews {
  text-align: center;
  color: var(--text-secondary);
  font-style: italic;
}

@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }
}
</style>
