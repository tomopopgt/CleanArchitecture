# 🚚 PathNode Pro - Clean Architecture & Dijkstra Path Engine

Android (Pure Kotlin / Jetpack Compose) で構築された、**Clean Architecture** に基づく最短経路探索（ダイクストラ法）アプリケーションです。

---

## 🏛️ Architecture & Package Structure

本プロジェクトは **Clean Architecture (関心の分離)** を厳格に適用し、プレゼンテーション層、ドメイン層、データ層を完全分離しています。

```mermaid
graph TD
    subgraph Presentation Layer
        UI[PathNodeScreen / Jetpack Compose]
        VM[PathNodeViewModel]
        Canvas[WarehouseMapCanvas]
    end

    subgraph Domain Layer
        RepoSpec[PathFinderRepository (Interface)]
        Models[GraphNode / GraphEdge / PathResult]
    end

    subgraph Data Layer
        Engine[DijkstraPathEngine]
    end

    UI --> VM
    VM --> RepoSpec
    Engine ..|> RepoSpec
    Engine --> Models