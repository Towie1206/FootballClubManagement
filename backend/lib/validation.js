const { ApiError } = require('./errors');

const POSITIONS = new Set(['GK', 'DF', 'MF', 'FW']);
const HEALTH_STATUSES = new Set(['Fit', 'Injured', 'Recovering', 'Suspended']);
const PLAYER_FIELDS = new Set([
    'fullName', 'position', 'jerseyNumber', 'healthStatus', 'ovr',
    'matches', 'goals', 'assists', 'mvp', 'club',
    'pac', 'sho', 'pas', 'dri', 'def', 'phy',
]);

function assertBody(body) {
    if (!body || typeof body !== 'object' || Array.isArray(body)) {
        throw new ApiError(400, 'VALIDATION_ERROR', 'Dữ liệu gửi lên không hợp lệ.', [
            { field: 'body', message: 'Phải là một JSON object.' },
        ]);
    }
}

function validateCredentials(body, { registration = false } = {}) {
    assertBody(body);
    const details = [];
    const username = typeof body.username === 'string' ? body.username.trim().toLowerCase() : '';
    const password = typeof body.password === 'string' ? body.password : '';

    const usernamePattern = /^[a-z0-9._-]+$/;
    if (registration) {
        if (username.length < 3 || username.length > 32 || !usernamePattern.test(username)) {
            details.push({ field: 'username', message: 'Tên đăng nhập phải có 3–32 ký tự: chữ, số, dấu chấm, gạch dưới hoặc gạch ngang.' });
        }
        if (password.length < 8 || password.length > 128 || !/[A-Za-z]/.test(password) || !/[0-9]/.test(password)) {
            details.push({ field: 'password', message: 'Mật khẩu phải có 8–128 ký tự, gồm ít nhất một chữ và một số.' });
        }
    } else {
        if (!username || username.length > 64) details.push({ field: 'username', message: 'Tên đăng nhập không hợp lệ.' });
        if (!password || password.length > 128) details.push({ field: 'password', message: 'Mật khẩu không hợp lệ.' });
    }

    if (details.length) throw new ApiError(400, 'VALIDATION_ERROR', 'Thông tin tài khoản không hợp lệ.', details);
    return { username, password };
}

function validatePlayerPayload(body, { partial = false } = {}) {
    assertBody(body);
    const value = {};
    const details = [];

    function stringField(name, { required = false, min = 0, max, transform } = {}) {
        if (!Object.prototype.hasOwnProperty.call(body, name)) {
            if (required) details.push({ field: name, message: 'Trường này là bắt buộc.' });
            return;
        }
        if (typeof body[name] !== 'string') {
            details.push({ field: name, message: 'Phải là chuỗi ký tự.' });
            return;
        }
        let normalized = body[name].trim();
        if (transform) normalized = transform(normalized);
        if (normalized.length < min || (max !== undefined && normalized.length > max)) {
            details.push({ field: name, message: `Độ dài phải từ ${min} đến ${max} ký tự.` });
            return;
        }
        value[name] = normalized;
    }

    function integerField(name, min, max) {
        if (!Object.prototype.hasOwnProperty.call(body, name)) return;
        if (!Number.isInteger(body[name]) || body[name] < min || body[name] > max) {
            details.push({ field: name, message: `Phải là số nguyên từ ${min} đến ${max}.` });
            return;
        }
        value[name] = body[name];
    }

    stringField('fullName', { required: !partial, min: 2, max: 100 });
    stringField('position', { required: !partial, min: 2, max: 2, transform: text => text.toUpperCase() });
    stringField('healthStatus', { min: 2, max: 20 });
    stringField('club', { min: 1, max: 100 });

    integerField('jerseyNumber', 0, 999);
    integerField('ovr', 0, 100);
    for (const field of ['matches', 'goals', 'assists', 'mvp']) integerField(field, 0, 1000000);
    for (const field of ['pac', 'sho', 'pas', 'dri', 'def', 'phy']) integerField(field, 0, 100);

    if (!partial && !Object.prototype.hasOwnProperty.call(body, 'jerseyNumber')) {
        details.push({ field: 'jerseyNumber', message: 'Trường này là bắt buộc.' });
    }
    if (value.position && !POSITIONS.has(value.position)) {
        details.push({ field: 'position', message: 'Chỉ chấp nhận GK, DF, MF hoặc FW.' });
    }
    if (value.healthStatus && !HEALTH_STATUSES.has(value.healthStatus)) {
        details.push({ field: 'healthStatus', message: 'Trạng thái sức khỏe không hợp lệ.' });
    }

    const recognizedCount = Object.keys(body).filter(key => PLAYER_FIELDS.has(key)).length;
    if (partial && recognizedCount === 0) {
        details.push({ field: 'body', message: 'Cần ít nhất một trường cầu thủ để cập nhật.' });
    }

    if (details.length) throw new ApiError(400, 'VALIDATION_ERROR', 'Dữ liệu cầu thủ không hợp lệ.', details);
    return value;
}

function validatePositiveId(rawId) {
    const id = Number(rawId);
    if (!Number.isSafeInteger(id) || id <= 0) {
        throw new ApiError(400, 'INVALID_ID', 'Mã cầu thủ không hợp lệ.');
    }
    return id;
}

function validateCoachPrompt(body) {
    assertBody(body);
    const prompt = typeof body.prompt === 'string' ? body.prompt.trim() : '';
    if (!prompt || prompt.length > 1000) {
        throw new ApiError(400, 'VALIDATION_ERROR', 'Câu hỏi AI phải có từ 1 đến 1000 ký tự.', [
            { field: 'prompt', message: 'Phải có từ 1 đến 1000 ký tự.' },
        ]);
    }
    return prompt;
}

module.exports = {
    validateCredentials,
    validatePlayerPayload,
    validatePositiveId,
    validateCoachPrompt,
};
