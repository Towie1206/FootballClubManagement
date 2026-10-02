class ApiError extends Error {
    constructor(status, code, message, details) {
        super(message);
        this.name = 'ApiError';
        this.status = status;
        this.code = code;
        this.details = details;
    }
}

function asyncHandler(handler) {
    return (req, res, next) => Promise.resolve(handler(req, res, next)).catch(next);
}

function sendError(res, status, code, message, details) {
    const error = { code, message };
    if (details !== undefined) error.details = details;
    return res.status(status).json({ error });
}

function notFoundHandler(req, res) {
    return sendError(res, 404, 'ROUTE_NOT_FOUND', 'Không tìm thấy API được yêu cầu.');
}

function errorHandler(err, req, res, next) { // eslint-disable-line no-unused-vars
    if (err instanceof ApiError) {
        return sendError(res, err.status, err.code, err.message, err.details);
    }

    if (err instanceof SyntaxError && err.status === 400 && Object.prototype.hasOwnProperty.call(err, 'body')) {
        return sendError(res, 400, 'INVALID_JSON', 'Nội dung JSON không hợp lệ.');
    }

    if (err && (err.type === 'entity.too.large' || err.status === 413)) {
        return sendError(res, 413, 'PAYLOAD_TOO_LARGE', 'Nội dung gửi lên vượt quá giới hạn cho phép.');
    }

    console.error('Unhandled API error:', err && err.stack ? err.stack : err);
    return sendError(res, 500, 'INTERNAL_ERROR', 'Máy chủ gặp lỗi. Vui lòng thử lại sau.');
}

module.exports = {
    ApiError,
    asyncHandler,
    sendError,
    notFoundHandler,
    errorHandler,
};
