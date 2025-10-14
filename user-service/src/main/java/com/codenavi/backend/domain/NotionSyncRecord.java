package com.codenavi.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "notion_sync_records")
@Getter
@Setter
@NoArgsConstructor
public class NotionSyncRecord extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solution_id", nullable = false)
    private Solution solution;

    @Embedded
    private SyncTarget syncTarget;

    @Embedded
    private NotionPage notionPage;

    @Embedded
    private SyncStatus syncStatus;

    @Embedded
    private SyncDetails syncDetails;

    @Embedded
    private VersionInfo versionInfo;


    // --- Inner Embeddable Classes ---

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    public static class SyncTarget {
        @Column(name = "st_type")
        private String type;

        @Lob
        @Column(name = "st_source_data")
        private String sourceData; // Store as JSON String

        @Column(name = "st_template_used")
        private String templateUsed;
    }

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    public static class NotionPage {
        @Column(name = "np_page_id")
        private String pageId;

        @Column(name = "np_page_url")
        private String pageUrl;

        @Column(name = "np_parent_id")
        private String parentId;

        @Column(name = "np_title")
        private String title;

        @Column(name = "np_created_at")
        private LocalDateTime createdAt;

        @Column(name = "np_last_edited_at")
        private LocalDateTime lastEditedAt;
    }

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    public static class SyncStatus {
        @Column(name = "ss_status")
        private String status;

        @Column(name = "ss_attempt_count")
        private Integer attemptCount;

        @Column(name = "ss_last_attempt_at")
        private LocalDateTime lastAttemptAt;

        @Lob
        @Column(name = "ss_error_message")
        private String errorMessage;
    }

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    public static class SyncDetails {
        @Column(name = "sd_blocks_synced")
        private Integer blocksSynced;

        @Column(name = "sd_properties_synced")
        private Integer propertiesSynced;

        @Column(name = "sd_processing_time")
        private Long processingTime;

        @Column(name = "sd_mcp_session_id")
        private String mcpSessionId;
    }

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    public static class VersionInfo {
        @Column(name = "vi_version_number")
        private Integer versionNumber;

        @Column(name = "vi_previous_version_id")
        private String previousVersionId;

        @Lob
        @Column(name = "vi_change_summary")
        private String changeSummary;
    }
}