# LandMap AI 🗺️🌾
**India Land Discovery, Cadastral Mapping & AI Valuation Engine**

> Built for College Hackathon Showcase. Discover land parcels across India, compare regional asking prices against government guidance values, inspect cadastral survey boundaries, and connect buyers and sellers directly.

---

## 🌟 Key Highlights & Problem Statement
Acquiring land in India is fraught with opacity:
1. **Unclear boundary demarcation & zoning uncertainties**
2. **Wide disparity between government circle rates (Guidance Value) and speculative market asking rates**
3. **Fragmented regional land metrics** (Sq.Ft in metros, Cents in South India, Gaj/Sq.Yards in North India, Gunthas/Acres)
4. **Scattered legal verification** (RERA, HMDA, BDA, DTCP, PMRDA, Clear Title)

**LandMap AI** solves this by unifying:
- **Interactive Cadastral Canvas Map**: Multi-touch survey grid map with arterial highway & metro line overlays, interactive parcel price pins, and boundary polygon highlights.
- **AI Price Intelligence Engine**: Transparent statistical valuation model predicting fair market rates (₹/sq.ft and total Lakhs/Crores) based on road width, corner frontage, and zoning parameters.
- **Side-by-Side Property Comparison**: Compare up to 3 parcels across prices, road frontage, facing direction, legal approvals, and an AI Value Winner recommendation.
- **Buyer & Seller Portals**: Direct WhatsApp/Call owner connections, site visit scheduling, and seller listing management.

---

## 🚀 Features Breakdown

### 1. Home & Exploration
- Search by City, Locality, or PIN Code (Bengaluru, Hyderabad, Pune, Chennai, Delhi-NCR).
- Quick category filters: *Residential Layouts*, *Gated Villa Communities*, *Managed Farmlands*, *Commercial Frontage*, *Logistics / Industrial*.
- Real estate visual theme with dark navy headings (`#0F2537`) and emerald accents (`#10B981`).

### 2. Interactive Map Mode
- Pan & zoom gesture support on an optimized Canvas layout.
- Cadastral survey grid layer & infrastructure highway corridors.
- Interactive pins with instant price tags (`₹68L`, `₹1.45Cr`, `₹52L`).
- Tap any marker to trigger the survey polygon boundary overlay and bottom detail drawer.

### 3. Listings & Multi-Facet Filters
- Real-time search by location and locality.
- Filter bottom sheet for:
  - Budget ranges (< ₹50L, < ₹75L, < ₹1.2Cr, < ₹2.0Cr)
  - Land categories
  - Planning approvals (RERA, HMDA, BDA, PMRDA, DTCP, Clear Title)
  - Corner plot only filter
- Sort by AI Recommended, Price (Low/High), Plot Area, or Best Value per Sq.Ft.

### 4. Technical Land Dossier & Seller Connect
- 4-point Cadastral Survey Boundary Sketch with North-up compass orientation.
- Indian land measurement converter: Sq.Ft ↔ Cents ↔ Sq.Yards (Gaj).
- Soil type & ground water table reporting.
- Direct seller contact: One-tap Phone dialer and pre-filled WhatsApp messaging.
- Site visit booking dialog with instant appointment confirmation.

### 5. AI Price Intelligence Engine
- Interactive valuation calculator with live adjusters for plot size, road width, corner frontage, and zoning.
- Fair price band with Min, Median, and Max estimates.
- Valuation Drivers analysis explaining percentage impacts (+12% for arterial roads, +8% for corner plots).
- Comparison table of **Govt Circle Rate** vs. **Market Asking** vs. **AI Fair Value**.
- 5-quarter historical asking price trend chart.
- Hackathon educational disclaimer clearly differentiating statistical models from bank appraisals.

### 6. Comparison Engine
- Side-by-side comparison matrix for up to 3 shortlisted properties.
- Automated **AI Value Winner** badge calculating the best value per square foot.

### 7. Dashboards & Listing Creation
- **Buyer Hub**: Saved properties, scheduled site visits, and tracked localities.
- **Seller Portal**: Active listing stats, view counts, and an interactive form to publish new land listings to the live app.

---

## 🛠️ Technology Stack
- **Framework**: Modern Android with Kotlin & Jetpack Compose
- **Design System**: Material Design 3 (M3) with custom real estate branding tokens
- **State Management**: Android MVVM architecture with `ViewModel` and Kotlin Coroutines/Flow
- **Graphics & Maps**: Custom Canvas rendering for Cadastral Survey polygons, coordinate systems, and price trend curves
- **Image Pipeline**: Coil Compose for land parcel previews and fallback drawables

---

## 📦 How to Run & Preview
1. **AI Studio Streaming Emulator**:
   - The app is running live in the AI Studio cloud streaming emulator.
2. **Export to GitHub**:
   - In AI Studio, open the top-right settings menu and select **Export / Push to GitHub** to link this project with `https://github.com/saicharitha53-dot/Landmap-AI`.
3. **Local Android Studio Build**:
   ```bash
   git clone https://github.com/saicharitha53-dot/Landmap-AI.git
   cd Landmap-AI
   # Open in Android Studio or compile via Gradle:
   gradle assembleDebug
   ```

---

## 📜 Disclaimer
*This project was developed for academic and hackathon demonstration purposes. Sample asking prices, circle rates, and AI valuations are illustrative statistical models and do not replace certified government cadastral survey checks or licensed bank appraisals.*
