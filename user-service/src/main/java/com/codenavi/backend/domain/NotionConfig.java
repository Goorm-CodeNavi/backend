package com.codenavi.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "notion_configs")
@Getter
@Setter
@NoArgsConstructor
public class NotionConfig extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Embedded
    private Connection connection;

    @Embedded
    private WorkspaceStructure workspaceStructure;

    @Embedded
    private SyncSettings syncSettings;

    @Embedded
    private TemplatePreferences templatePreferences;


    // --- Inner Embeddable Classes ---

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Connection {
        @Column(name = "conn_notion_token")
        private String notionToken; // Encryption should be handled at the service layer

        @Column(name = "conn_integration_id")
        private String integrationId;

        @Column(name = "conn_workspace_id")
        private String workspaceId;

        @Column(name = "conn_workspace_name")
        private String workspaceName;

        @Column(name = "conn_connected_at")
        private LocalDateTime connectedAt;

        @Column(name = "conn_status")
        private String connectionStatus;
    }

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    public static class WorkspaceStructure {
        @Column(name = "ws_root_page_id")
        private String rootPageId;

        @Embedded
        private Databases databases;

        @Embedded
        private Pages pages;
    }

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Databases {
        @Column(name = "ws_db_problems_id")
        private String problemsDbId;
        @Column(name = "ws_db_learning_log_id")
        private String learningLogDbId;
        @Column(name = "ws_db_algorithms_notes_id")
        private String algorithmsNotesDbId;
        @Column(name = "ws_db_ai_feedback_id")
        private String aiFeedbackDbId;
    }

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Pages {
        @Column(name = "ws_page_dashboard_id")
        private String dashboardPageId;
        @Column(name = "ws_page_statistics_id")
        private String statisticsPageId;
        @Column(name = "ws_page_goals_id")
        private String goalsPageId;
    }


    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    public static class SyncSettings {
        @Column(name = "sync_auto_enabled")
        private Boolean autoSyncEnabled;

        @Column(name = "sync_thinking_process")
        private Boolean syncThinkingProcess;

        @Column(name = "sync_code")
        private Boolean syncCode;

        @Column(name = "sync_ai_feedback")
        private Boolean syncAiFeedback;

        @Column(name = "sync_frequency")
        private String syncFrequency;
    }

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    public static class TemplatePreferences {
        @Column(name = "tp_default_problem_template")
        private String defaultProblemTemplate;

        @JdbcTypeCode(SqlTypes.JSON)
        @Column(name = "tp_custom_templates")
        private List<String> customTemplates;
    }
}

