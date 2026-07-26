![Android CI](https://github.com/tomopopgt/CleanArchitecture/actions/workflows/android-ci.yml/badge.svg)

# 🚚 PathNode Pro - Clean Architecture & Dijkstra Path Engine

Android (Pure Kotlin / Jetpack Compose) で構築された、**Clean Architecture** に基づく最短経路探索（ダイクストラ法）アプリケーションです。

---

## 🏛️ Architecture & Package Structure

本プロジェクトは **Clean Architecture (関心の分離)** を厳格に適用し、プレゼンテーション層、ドメイン層、データ層を完全分離しています。

```mermaid
graph TD
    subgraph Presentation["Presentation Layer"]
        UI["PathNodeScreen / Jetpack Compose"]
        VM["PathNodeViewModel"]
        Canvas["WarehouseMapCanvas"]
    end

    subgraph Domain["Domain Layer"]
        UC["GetShortestPathUseCase"]
        Repo["PathFinderRepository"]
        Model["GraphNode / GraphEdge / PathResult"]
    end

    subgraph Data["Data Layer"]
        Engine["DijkstraPathEngine"]
    end

    UI --> VM
    VM --> Canvas
    VM --> UC
    UC --> Repo
    Engine ..|> Repo
    UC --> Model