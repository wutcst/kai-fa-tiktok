package cn.edu.whut.sept.zuul.model;

/**
 * 传输房间类。
 * 玩家进入此房间后，会被随机传送到地图上的另一个房间。
 */
public class TransporterRoom extends Room {

    /**
     * 构造传输房间。
     * @param description 描述信息
     */
    public TransporterRoom(String description) {
        super(description);
    }

    /**
     * 重写详细描述。
     * 当玩家看（look）或进入时，给予危险或神秘的提示。
     */
    @Override
    public String getLongDescription() {
        return "【特殊场景】这是一间充满空间扭曲感的实验室...\n" + super.getLongDescription();
    }
}