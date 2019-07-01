package com.fengjr.cock.manage.domain.modules.Server;

public class ServerInformation {

        //ip端口
        private String ipPort;
        //服务器状态
        private String status;
        //正在运行的task数量
        private String runingTaskNums;
        //总的task数量
        private String totalTaskNums;

        public String getIpPort() {
            return ipPort;
        }

        public void setIpPort(String ipPort) {
            this.ipPort = ipPort;
        }

        public String getStatus() {
            status = "正常运行";
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }


        public String getTotalTaskNums() {
            return totalTaskNums;
        }

        public void setTotalTaskNums(String totalTaskNums) {
            this.totalTaskNums = totalTaskNums;
        }

        public String getRuningTaskNums() {
            return runingTaskNums;
        }

        public void setRuningTaskNums(String runingTaskNums) {
            this.runingTaskNums = runingTaskNums;
        }
}
