local userId = ARGV[1]
local userLock = "user:lock:" .. userId
local userKey = "vol:user:" .. userId .. ":character:" .. characterId
local characterId = ARGV[2]
local volKey = "character:" .. characterId

if(redis.call('exists', userKey) == 1 & redis.call('exists',userLock) == 1) then
    redis.call('del', userKey)
    redis.call('hincrby', volKey, 'volume', 1)
    return 1
end

return 0

