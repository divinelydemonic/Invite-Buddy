# 🎉 Invitation Manager App - InviteBuddy

A modern Android application built with **Kotlin** and **Jetpack Compose** to manage invitations with elegant UI, smooth animations, and real-time event tracking.

This project focuses on performance, UI polish, micro-interactions, and scalable event management architecture.

---

# 📱 Overview

Invitation Manager helps users:

- Track event invitations
- Monitor countdowns in real-time
- Visually identify urgent events
- Experience delightful UI feedback through animation

It is designed as a scalable foundation for a full event management system.

---

# 🎥 Demo 

## 🖥️ Snapshots

<h2 align="center">📸 App Snapshots</h2>

<p align="center">
  <img src="snapshots/1.png" width="260">
  <img src="snapshots/2.png" width="260">
</p>

<p align="center">
  <img src="snapshots/3.png" width="260">
  <img src="snapshots/4.png" width="260">
</p>

## 📌 Main App Demo

<b>YouTube link : </b> https://youtu.be/1QDIzo64lFg

Shows:
- Adding invitations
- Countdown behavior
- Pulse urgency animation
- Scroll-aware FAB
- UI transitions
- Theme handling

---

## 🎆 Confetti Animation Demo

[<confetti_video>](https://github.com/user-attachments/assets/a03e121d-0aa0-490f-9147-79303bbeee26)

This video demonstrates:
- Confetti celebration trigger
- Success state animation
- Smooth particle rendering using Compose animation APIs

---

# 🛠 Debug Mode for Testing

To properly test animations without waiting for the actual event date, a temporary debug override was introduced inside the `EventCountdown` composable.

```kotlin
val debugForceConfetti = true
```

---

# ✨ Features

## 🎊 Invitation Tracking
- Add and manage invitations
- Dynamic total invitation counter
- Structured card-based layout
- Clean Material 3 UI

## ⏳ Live Event Countdown
- Real-time countdown timer
- Automatic UI updates
- Time-sensitive event tracking

## 🔥 Pulse Urgency Animation
- Subtle pulsing effect for near-expiry events
- Draws attention without overwhelming UX
- Implemented using Compose infinite transitions

## 🎆 Confetti Celebration Animation
- Triggered on successful event actions
- Animated particle effect using Canvas
- Enhances positive user feedback

## 📱 Scroll-Aware Floating Action Button
- FAB hides while scrolling
- Reappears when scrolling stops
- Improves content visibility

## 🎨 UI/UX Polish
- Material 3 theming
- Light and Dark mode support
- Clean spacing and divider management
- Smooth state-driven animations

---

# 🧱 Tech Stack

- Kotlin
- Jetpack Compose
- Material 3
- Compose Animation APIs
- Canvas drawing
- State-driven UI architecture

---

# 🏗 Architecture Philosophy

The app follows:

- Declarative UI principles
- State hoisting where appropriate
- Separation of UI and business logic
- Scalable structure for future data persistence

Currently uses in-memory state management.

---

# 🚀 Upcoming Features

## 🗂 Room Database Integration

- Persistent local storage
- Offline-first capability
- Structured entity models
- DAO-based data access

## 🎫 Multiple Event Invitation Management

- Manage invitations across multiple events
- Event-based grouping
- Event detail screen
- Scalable relational structure

---

# 📦 Future Expansion Ideas

- Event categories
- Notification reminders
- Shareable event summaries
- Cloud sync capability
- Authentication layer

