CREATE TABLE IF NOT EXISTS submissions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid (),
    code TEXT NOT NULL,
    roast_mode BOOLEAN NOT NULL DEFAULT false,
    score INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS analyses (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid (),
    submission_id UUID NOT NULL REFERENCES submissions (id) ON DELETE CASCADE,
    severity VARCHAR(20) NOT NULL, -- 'critical', 'warning', 'good'
    feedback_message TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS fix_suggestions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid (),
    analysis_id UUID NOT NULL REFERENCES analyses (id) ON DELETE CASCADE,
    original_code TEXT,
    suggested_code TEXT NOT NULL,
    explanation TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Index for the Shame Leaderboard queries (ordering by score ascending for worst code)
CREATE INDEX IF NOT EXISTS idx_submissions_score ON submissions (score);