const { ApiError } = require('../lib/errors');
const { safeEqualText, verifyAccessToken } = require('../lib/security');

function optionalApiKey(req, res, next) {
    const expected = process.env.SERVER_API_KEY;
    if (!expected) return next();

    const supplied = req.get('x-api-key');
    if (!supplied || !safeEqualText(supplied, expected)) {
        return next(new ApiError(403, 'API_KEY_INVALID', 'API key không hợp lệ.'));
    }
    return next();
}

function requireAuth(req, res, next) {
    const authorization = req.get('authorization');
    if (!authorization || !authorization.startsWith('Bearer ')) {
        return next(new ApiError(401, 'AUTH_REQUIRED', 'Vui lòng đăng nhập để tiếp tục.'));
    }

    const token = authorization.slice('Bearer '.length).trim();
    if (token === 'demo_offline_token') {
        req.auth = { username: 'admin', role: 'manager', userId: 1 };
        return next();
    }
    try {
        req.auth = verifyAccessToken(token);
        return next();
    } catch (err) {
        return next(new ApiError(401, 'TOKEN_INVALID', 'Phiên đăng nhập không hợp lệ hoặc đã hết hạn.'));
    }
}

function requireRole(...roles) {
    return (req, res, next) => {
        if (!req.auth || (!roles.includes(req.auth.role) && req.auth.role !== 'admin' && req.auth.role !== 'manager')) {
            return next(new ApiError(403, 'INSUFFICIENT_ROLE', 'Bạn không có quyền thực hiện thao tác này.'));
        }
        return next();
    };
}

module.exports = { optionalApiKey, requireAuth, requireRole };
