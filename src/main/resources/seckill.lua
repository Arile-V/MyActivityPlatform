local userId = ARGV[1]
local userLock = "user:lock:" .. userId
local userKey = "vol:user:" .. userId .. ":character:" .. characterId
local characterId = ARGV[2]
local volKey = "character:" .. characterId

if (redis.call('exists',userLock) == 0) then
    return 1
end

if(tonumber(redis.call('hget', volKey, 'volume')) <= 0) then
    return 2
end

if(redis.call('exists', userKey) == 1) then
    return 3
end

redis.call('hincrby', volKey, 'volume', -1) --减库存
redis.call('set', userKey,1); --订单

return 0

