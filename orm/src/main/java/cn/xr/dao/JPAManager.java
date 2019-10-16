package cn.xr.dao;

interface JPAManager {

    public <T extends JPAContext> T getPersistenceService(Class<T> clazz);
}
