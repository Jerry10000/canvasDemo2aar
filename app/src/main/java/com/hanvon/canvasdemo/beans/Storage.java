package com.hanvon.canvasdemo.beans;


import com.hanvon.canvasdemo.beans.Point;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by pc on 2017/10/30.
 */

public class Storage
{
    // 仓库最大存储量
    private final int MAX_SIZE = 2000;

    // 仓库存储的载体
    private LinkedList<Point> list = new LinkedList<Point>();
    // 仓库存储的载体副本
    private LinkedList<Point> listClone = new LinkedList<Point>();

    // 锁
    private final Lock lock = new ReentrantLock();

    // 仓库满的条件变量
    private final Condition full = lock.newCondition();

    // 仓库空的条件变量
    private final Condition empty = lock.newCondition();

    // 生产num个产品
    public void produce(Point point)
    {
        // 获得锁
        lock.lock();

        //生产条件不满足，仓库存储过多
        while (list.size() + 1 > MAX_SIZE)
        {
            System.out.println("点存储太多，已存:" + list.size()/2  + " 个点");
            try
            {
                // 由于条件不满足，生产阻塞
                full.await();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }

        // 生产条件满足情况下
        list.add(point);

        // 唤醒其他所有线程
        full.signalAll();
        empty.signalAll();

        // 释放锁
        lock.unlock();
    }

    // 取出仓库中的所有产品
    public LinkedList<Point> consume()
    {

        // 获得锁
        lock.lock();

        // 如果仓库存储量不足
        if (list.size() == 0)
        {
            System.out.println("仓库中没有点");
//            try
//            {
//                // 由于条件不满足，消费阻塞
//                empty.await();
//            }
//            catch (InterruptedException e)
//            {
//                e.printStackTrace();
//            }
        }

        // 消费条件满足情况下，取出所有点
        listClone.addAll((LinkedList<Point>)list.clone());
        list.clear();

        // 唤醒其他所有线程
        full.signalAll();
        empty.signalAll();

        // 释放锁
        lock.unlock();

        return listClone;
    }

    // set/get方法
    public int getMAX_SIZE()
    {
        return MAX_SIZE;
    }

    public LinkedList<Point> getList()
    {
        return list;
    }

    public void setList(LinkedList<Point> list)
    {
        this.list = list;
    }
}
