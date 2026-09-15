# 🚀 Deployment Guide: Render (Backend) & Vercel (Frontend)

This guide walks you through deploying **PlacementAI Assistant** with:
- **Backend (Spring Boot + Java 21)** on **Render** (Free Web Service / Docker).
- **Frontend (React + Vite + TanStack Router)** on **Vercel** (Global Edge CDN).

---

## Part 1: Deploying the Backend on Render

### Step 1: Create a Web Service on Render
1. Go to your [Render Dashboard](https://dashboard.render.com/).
2. Click **New +** → **Web Service**.
3. Connect your GitHub repository: `azharkhan924/Placement-Ai-Assistant`.
4. Configure the service settings:
   - **Name**: `placement-ai-backend` (or your choice)
   - **Region**: Select your preferred region (e.g., *Oregon (US West)* or *Frankfurt (EU)*)
   - **Branch**: `main`
   - **Root Directory**: Leave blank (default)
   - **Environment**: **Docker**
   - **Dockerfile Path**: `Dockerfile` (or leave default)
   - **Instance Type**: **Free**

### Step 2: Configure Environment Variables on Render
Under **Environment Variables** in your Render service settings, add:

| Key | Value | Notes |
|---|---|---|
| `PORT` | `10000` | Render allocates this automatically, Spring Boot will bind to it. |
| `CORS_ALLOWED_ORIGINS` | `https://*.vercel.app,https://vercel.app` | Allows your Vercel deployment to communicate with the backend. |
| `JWT_SECRET` | *(Generate a 256-bit secret or leave default)* | Example: `dGVzdHNlY3JldGtleXRlc3RzZWNyZXRrZXl0ZXN0c2VjcmV0a2V5MTI=` |
| `GEMINI_API_KEY` | *(Your Google Gemini API Key)* | Optional for Gemini AI generation features. |
| `GEMINI_MODEL` | `gemini-1.5-flash` | Default Gemini model. |

> [!TIP]
> **Database Options on Render**:
> - **Default (Zero Setup)**: By default, the application runs an embedded H2 database in PostgreSQL compatibility mode at `./data/placement_ai`.
> - **Render Managed PostgreSQL (Recommended for Persistent Production Data)**:
>   1. In Render Dashboard, click **New +** → **PostgreSQL Database** (Free Tier).
>   2. Copy the **Internal Database URL** (e.g. `postgres://user:password@hostname/dbname`).
>   3. In your Web Service environment variables, set:
>      - `DB_URL`: `jdbc:postgresql://<hostname>:5432/<dbname>`
>      - `DB_USERNAME`: `<user>`
>      - `DB_PASSWORD`: `<password>`
>      - `DB_DRIVER`: `org.postgresql.Driver`

### Step 3: Deploy & Copy Backend URL
- Click **Create Web Service**.
- Render will pull your repo, run the multi-stage Docker build, and start Spring Boot.
- Once deployed, copy your live backend URL (e.g., `https://placement-ai-backend.onrender.com`).

---

## Part 2: Deploying the Frontend on Vercel

### Step 1: Import Project into Vercel
1. Go to your [Vercel Dashboard](https://vercel.com/dashboard).
2. Click **Add New...** → **Project**.
3. Import the GitHub repository: `azharkhan924/Placement-Ai-Assistant`.

### Step 2: Configure Vercel Project Settings
In the **Configure Project** screen:
- **Project Name**: `placement-ai-assistant`
- **Framework Preset**: **Vite** (or **Other**)
- **Root Directory**: Click **Edit** and select **`frontend`** ⚠️ *(Crucial step!)*
- **Build and Output Settings**:
  - **Build Command**: `npm run build:vercel` (or `NITRO_PRESET=vercel vite build`)
  - **Output Directory**: Automatically handled by Nitro / `.vercel/output`
  - **Install Command**: `npm install`

### Step 3: Add Frontend Environment Variables on Vercel
In the **Environment Variables** section, add:

| Key | Value | Description |
|---|---|---|
| `VITE_API_URL` | `https://placement-ai-backend.onrender.com` | Your live Render backend URL from Part 1. *(Do not include a trailing slash)* |

### Step 4: Deploy & Verify
1. Click **Deploy**.
2. Vercel will install dependencies, build the client and serverless assets, and publish to a `.vercel.app` domain.
3. Once live, click on the generated URL (e.g., `https://placement-ai-assistant.vercel.app`).
4. Register a new user, upload a resume in the **AI Resume Analyzer**, take a test in **Aptitude Training**, and practice answers in **HR Interview Practice**!

---

## Quick Troubleshooting Checklist

| Issue | Root Cause | Fix |
|---|---|---|
| **CORS error in browser console** (`No 'Access-Control-Allow-Origin'`) | Backend doesn't recognize your Vercel URL | In Render Environment Variables, add your exact Vercel URL to `CORS_ALLOWED_ORIGINS` (e.g. `https://placement-ai-assistant.vercel.app,https://*.vercel.app`). |
| **Backend deployment times out on Render** | Spring Boot binding to wrong port | Ensure `PORT` is defined in Render env vars (`10000`); `application.properties` uses `server.port=${PORT:8080}`. |
| **Vercel returns 404 on page refresh** (e.g. `/aptitude`) | SPA deep routes need rewriting | `frontend/vercel.json` contains the rewrite configuration. Ensure `build:vercel` was executed. |
| **Backend cold start delay (50 seconds)** | Render Free Tier spins down after 15 minutes of inactivity | Normal behavior on free tier; the server will automatically wake up upon the first HTTP request. |
