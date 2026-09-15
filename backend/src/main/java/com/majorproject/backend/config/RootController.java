package com.majorproject.backend.config;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Root and Health Controller to provide informative feedback when visiting
 * the Render backend URL directly in a web browser.
 */
@RestController
public class RootController {

    @GetMapping(value = {"/", "/health"}, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_HTML_VALUE})
    public ResponseEntity<?> healthCheck(@RequestHeader(value = "Accept", defaultValue = "") String acceptHeader) {
        if (acceptHeader.contains(MediaType.APPLICATION_JSON_VALUE) && !acceptHeader.contains(MediaType.TEXT_HTML_VALUE)) {
            return ResponseEntity.ok(Map.of(
                    "status", "UP",
                    "service", "PlacementAI Spring Boot Backend",
                    "version", "1.0.0",
                    "endpoints", Map.of(
                            "auth", "/api/auth/**",
                            "resume", "/api/resume/**",
                            "aptitude", "/api/aptitude/**",
                            "hr", "/api/hr/**",
                            "profile", "/api/profile/**"
                    )
            ));
        }

        String html = """
            <!DOCTYPE html>
            <html lang="en">
            <head>
              <meta charset="UTF-8">
              <meta name="viewport" content="width=device-width, initial-scale=1.0">
              <title>PlacementAI Backend API — Live</title>
              <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;600;700&display=swap" rel="stylesheet">
              <style>
                body {
                  margin: 0;
                  padding: 0;
                  font-family: 'Plus Jakarta Sans', sans-serif;
                  background: #0f172a;
                  color: #f8fafc;
                  display: flex;
                  align-items: center;
                  justify-content: center;
                  min-height: 100vh;
                }
                .card {
                  background: #1e293b;
                  border: 1px solid #334155;
                  border-radius: 20px;
                  padding: 40px;
                  max-width: 580px;
                  margin: 20px;
                  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.5);
                }
                .badge {
                  display: inline-flex;
                  align-items: center;
                  gap: 8px;
                  background: rgba(16, 185, 129, 0.15);
                  color: #34d399;
                  padding: 6px 14px;
                  border-radius: 9999px;
                  font-size: 13px;
                  font-weight: 600;
                  margin-bottom: 20px;
                }
                .badge-dot {
                  width: 8px;
                  height: 8px;
                  background: #10b981;
                  border-radius: 50%;
                  box-shadow: 0 0 10px #10b981;
                }
                h1 {
                  font-size: 26px;
                  margin: 0 0 12px 0;
                  color: #ffffff;
                }
                p {
                  color: #94a3b8;
                  line-height: 1.6;
                  font-size: 15px;
                  margin: 0 0 20px 0;
                }
                .highlight-box {
                  background: rgba(249, 115, 22, 0.1);
                  border: 1px solid rgba(249, 115, 22, 0.25);
                  border-radius: 12px;
                  padding: 16px;
                  margin-bottom: 24px;
                  color: #fdba74;
                  font-size: 14px;
                  line-height: 1.5;
                }
                .endpoints {
                  background: #0f172a;
                  border-radius: 12px;
                  padding: 16px;
                  margin-bottom: 24px;
                }
                .endpoint-row {
                  display: flex;
                  justify-content: space-between;
                  font-family: monospace;
                  font-size: 13px;
                  padding: 6px 0;
                  border-bottom: 1px solid #1e293b;
                }
                .endpoint-row:last-child {
                  border-bottom: none;
                }
                .method { color: #38bdf8; font-weight: bold; }
                .path { color: #cbd5e1; }
                .btn {
                  display: block;
                  text-align: center;
                  background: #f97316;
                  color: #ffffff;
                  text-decoration: none;
                  font-weight: 600;
                  padding: 14px;
                  border-radius: 9999px;
                  transition: background 0.2s;
                }
                .btn:hover { background: #ea580c; }
              </style>
            </head>
            <body>
              <div class="card">
                <div class="badge">
                  <div class="badge-dot"></div>
                  Spring Boot Backend is Live
                </div>
                <h1>PlacementAI Backend API</h1>
                <p>
                  You have successfully reached the <strong>Render REST API service</strong> for PlacementAI.
                </p>
                <div class="highlight-box">
                  <strong>Looking for the student user interface?</strong><br>
                  The interactive web application (login, register, AI resume scanner, and test modules) is hosted on <strong>Vercel</strong>!
                </div>
                <div class="endpoints">
                  <div class="endpoint-row"><span class="method">POST</span><span class="path">/api/auth/register</span></div>
                  <div class="endpoint-row"><span class="method">POST</span><span class="path">/api/auth/login</span></div>
                  <div class="endpoint-row"><span class="method">POST</span><span class="path">/api/resume/analyze</span></div>
                  <div class="endpoint-row"><span class="method">GET</span><span class="path">/api/aptitude/mock-test</span></div>
                  <div class="endpoint-row"><span class="method">POST</span><span class="path">/api/hr/evaluate</span></div>
                </div>
                <p style="font-size: 13px; color: #64748b; text-align: center;">
                  To access the student interface, open your Vercel URL (e.g. <code>https://&lt;your-app&gt;.vercel.app</code>)
                </p>
              </div>
            </body>
            </html>
            """;

        return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(html);
    }

    @GetMapping(value = {"/login", "/register"}, produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> frontendNotice() {
        String html = """
            <!DOCTYPE html>
            <html lang="en">
            <head>
              <meta charset="UTF-8">
              <meta name="viewport" content="width=device-width, initial-scale=1.0">
              <title>Backend API Notice — PlacementAI</title>
              <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;600;700&display=swap" rel="stylesheet">
              <style>
                body {
                  margin: 0;
                  padding: 0;
                  font-family: 'Plus Jakarta Sans', sans-serif;
                  background: #0f172a;
                  color: #f8fafc;
                  display: flex;
                  align-items: center;
                  justify-content: center;
                  min-height: 100vh;
                }
                .card {
                  background: #1e293b;
                  border: 1px solid #334155;
                  border-radius: 20px;
                  padding: 40px;
                  max-width: 560px;
                  margin: 20px;
                  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.5);
                  text-align: center;
                }
                .icon {
                  font-size: 44px;
                  margin-bottom: 16px;
                }
                h1 {
                  font-size: 24px;
                  margin: 0 0 12px 0;
                  color: #ffffff;
                }
                p {
                  color: #94a3b8;
                  line-height: 1.6;
                  font-size: 15px;
                  margin: 0 0 24px 0;
                }
                .info-banner {
                  background: rgba(56, 189, 248, 0.1);
                  border: 1px solid rgba(56, 189, 248, 0.25);
                  border-radius: 12px;
                  padding: 16px;
                  margin-bottom: 24px;
                  color: #7dd3fc;
                  font-size: 14px;
                  text-align: left;
                  line-height: 1.5;
                }
              </style>
            </head>
            <body>
              <div class="card">
                <div class="icon">🚀</div>
                <h1>This URL is the Render API Backend</h1>
                <p>
                  You navigated to <code>placement-ai-assistant.onrender.com/login</code>. This server provides Spring Boot REST API endpoints, while the student user interface is deployed on <strong>Vercel</strong>.
                </p>
                <div class="info-banner">
                  <strong>Where to go:</strong><br>
                  Open your <strong>Vercel deployment URL</strong> (for example <code>https://&lt;your-project-name&gt;.vercel.app/login</code>) to sign in, practice aptitude, and scan resumes.
                </div>
                <p style="font-size: 13px; color: #64748b;">
                  Backend is healthy & operational on Render.
                </p>
              </div>
            </body>
            </html>
            """;

        return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(html);
    }
}
