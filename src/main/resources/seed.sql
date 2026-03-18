-- ---------------------------------------------------------
-- FAKE DATA FOR DEVROAST (Seed)
-- ---------------------------------------------------------

-- 1) Create 3 mock submissions with hardcoded UUIDs so we can reference them below
INSERT INTO submissions (id, code, roast_mode, score) VALUES 
(
    '11111111-1111-1111-1111-111111111111', 
    'public class Main {
    public static void main(String[] args) {
        try {
            System.out.println("Hello World");
        } catch(Exception e) {
        }
    }
}', 
    true, 
    999 -- Extremely high shame score
),
(
    '22222222-2222-2222-2222-222222222222', 
    'const connectDB = () => {
    const db = "postgres://admin:password123@localhost:5432/mydb";
    return db;
}', 
    false, 
    850 -- Very critical security issue
),
(
    '33333333-3333-3333-3333-333333333333', 
    'def get_user_age(user):
    # This checks the age
    if user.age > 18:
        return True
    else:
        return False', 
    true, 
    300 -- Just silly, could be a one-liner
);


-- 2) Create Analyses for the above submissions
INSERT INTO analyses (id, submission_id, severity, feedback_message) VALUES 
(
    'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
    '11111111-1111-1111-1111-111111111111',
    'warning',
    'Empty catch block? Wow, bold move. I guess exceptions just don''t happen on your machine. Ignorance is bliss until production goes down.'
),
(
    'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
    '22222222-2222-2222-2222-222222222222',
    'critical',
    'You just hardcoded your database password in plaintext. I hope your resume is updated because this is how data breaches happen.'
),
(
    'cccccccc-cccc-cccc-cccc-cccccccccccc',
    '33333333-3333-3333-3333-333333333333',
    'good', // Using 'good' just to show we can, though roast mode might find something
    'Wow, an if/else block that returns true/false. Have you heard of returning the boolean expression directly, or do you get paid by the line?'
);


-- 3) Create Fix Suggestions linked to the analyses
INSERT INTO fix_suggestions (id, analysis_id, original_code, suggested_code, explanation) VALUES 
(
    'dddddddd-dddd-dddd-dddd-dddddddddddd',
    'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
    '} catch(Exception e) {
}',
    '} catch(Exception e) {
    // At minimum, log the error
    e.printStackTrace(); 
}',
    'Empty catch blocks swallow exceptions silently, making debugging virtually impossible when things inevitably fail.'
),
(
    'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee',
    'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
    'const db = "postgres://admin:password123@localhost:5432/mydb";',
    'const db = process.env.DATABASE_URL;',
    'Never commit sensitive credentials to version control. Always read them from environment variables via a .env file or deployment secrets.'
),
(
    'ffffffff-ffff-ffff-ffff-ffffffffffff',
    'cccccccc-cccc-cccc-cccc-cccccccccccc',
    'if user.age > 18:
    return True
else:
    return False',
    'return user.age > 18',
    'The expression `user.age > 18` already evaluates to a boolean. There is no need for an if/else block to return True or False.'
);
