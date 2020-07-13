package com.nowcoder.community;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class BlockQueueTests {
    public static void main(String[] args) {
        //一个生产者和三个消费者
        BlockingQueue queue = new ArrayBlockingQueue(10);//队列容量
        new Thread(new Producer(queue)).start();//生产者线程生产数据
        new Thread(new Consumer(queue)).start();//消费者
        new Thread(new Consumer(queue)).start();
        new Thread(new Consumer(queue)).start();


    }
}


class Producer implements  Runnable{

    private BlockingQueue<Integer> queue;

    public Producer(BlockingQueue<Integer> queue){
        this.queue = queue;
    }

    @Override
    public void run(){
        try {
            for(int i = 0;i < 100; i++){
                //每20毫秒生产一个数值i
                Thread.sleep(20);
                queue.put(i);
                System.out.println(Thread.currentThread().getName() + "生产" + queue.size());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

class Consumer implements Runnable{

    private BlockingQueue<Integer> queue;

    public Consumer(BlockingQueue<Integer> queue){
        this.queue = queue;
    }

    @Override
    public void run(){
        try{
            while(true){
                //0-1000之间的随机数
                Thread.sleep(new Random().nextInt(1000));
                queue.take();
                System.out.println(Thread.currentThread().getName() + "消费了：" + queue.size());
            }
        }catch (Exception e){

        }
    }
}