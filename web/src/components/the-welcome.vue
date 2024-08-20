<template>
  <a-row :gutter="16" :style="{marginBottom: '25px'}">
    <a-col :span="24">
      <a-card>
        <a-row :style="{overflow: 'hidden'}">
          <a-col :span="8">
            <a-statistic
                title="Total views"
                :value="statistics.viewCount"
            >
              <template #suffix>
                <user-outlined :style="{fontSize: '80%', marginLeft: '5px'}"/>
              </template>
            </a-statistic>
          </a-col>
          <a-col :span="8">
            <a-statistic
                title="Total likes"
                :value="statistics.voteCount"
            >
              <template #suffix>
                <like-outlined :style="{fontSize: '80%', marginLeft: '5px'}"/>
              </template>
            </a-statistic>
          </a-col>
          <a-col :span="8">
            <a-statistic
                title="Likes as percentage of views"
                :value="statistics.voteCount / statistics.viewCount * 100"
                :precision="2"
                suffix="%"
                :value-style="{ color: '#cf1322' }"
            >
            </a-statistic>
          </a-col>
        </a-row>
      </a-card>
    </a-col>
  </a-row>
  <a-row :gutter="16">
    <a-col :span="12">
      <a-card>
        <a-row>
          <a-col :span="12">
            <a-statistic
                title="Views today"
                :value="statistics.todayViewCount"
            >
              <template #suffix>
                <user-outlined :style="{fontSize: '80%', marginLeft: '5px'}"/>
              </template>
            </a-statistic>
          </a-col>
          <a-col :span="12">
            <a-statistic
                title="Likes today"
                :value="statistics.todayVoteCount"
            >
              <template #suffix>
                <like-outlined :style="{fontSize: '80%', marginLeft: '5px'}"/>
              </template>
            </a-statistic>
          </a-col>
        </a-row>
      </a-card>
    </a-col>
    <a-col :span="12">
      <a-card>
        <a-row>
          <a-col :span="12">
            <a-statistic
                title="Forecasted views for today"
                :value="statistics.todayViewIncrease"
            >
              <template #suffix>
                <user-outlined :style="{fontSize: '80%', marginLeft: '5px'}"/>
              </template>
            </a-statistic>
          </a-col>
          <a-col :span="12">
            <a-statistic
              v-if="todayViewIncreaseRateNA"
              title="Today's forecasted view increase rate"
              value="N/A"
            ></a-statistic>
            <a-statistic
                v-if="!todayViewIncreaseRateNA"
                title="Today's forecasted view increase rate"
                :value="statistics.todayViewIncreaseRateAbs"
                :precision="2"
                suffix="%"
                :value-style="statistics.todayViewIncreaseRate >= 0 ? { color: '#cf1322' } : { color: '#3f8600' }"
            >
              <template #prefix>
                <arrow-down-outlined v-if="statistics.todayViewIncreaseRate < 0"/>
                <arrow-up-outlined v-if="statistics.todayViewIncreaseRate >= 0"/>
              </template>
            </a-statistic>
          </a-col>
        </a-row>
      </a-card>
    </a-col>
  </a-row>
  <br>
  <a-row>
    <a-col :span="24">
      <div id="main" :style="{width: '100%', height: '300px'}"></div>
    </a-col>
  </a-row>
</template>


<script lang="ts">
import {defineComponent, onMounted, ref} from 'vue';
import axios from "axios";
import * as echarts from 'echarts';
import { message } from 'ant-design-vue';








export default defineComponent({
  name: 'the-welcome',
  setup() {
    const statistics:any = ref({});
    const statistics30day:any = ref([]);
    const todayViewIncreaseRateNA = ref(false);



    const getStatistics = () => {
      return axios.get("/ebook-snapshot/statistics")
          .then((resp) => {
            const json = resp.data;
            if (json.success) {
              const content = json.content;
              statistics.value.viewCount = content[1].viewCount;
              statistics.value.voteCount = content[1].voteCount;
              statistics.value.todayViewCount = content[1].viewIncrease;
              statistics.value.todayVoteCount = content[1].voteIncrease;

              // 按分钟计算当前时间点，占一天的百分比
              const now = new Date();
              const nowRate = (now.getHours() * 60 + now.getMinutes()) / (60 * 24);
              // console.log(nowRate)
              statistics.value.todayViewIncrease = parseInt(String(content[1].viewIncrease / nowRate));
              // todayViewIncreaseRate：今日预计增长率
              if (content[0].viewIncrease === 0) {
                todayViewIncreaseRateNA.value = true;
              }
              statistics.value.todayViewIncreaseRate = (statistics.value.todayViewIncrease - content[0].viewIncrease) / content[0].viewIncrease * 100;
              statistics.value.todayViewIncreaseRateAbs = Math.abs(statistics.value.todayViewIncreaseRate);
            } else {
              message.error(json.message);
            }
          })
    }


    const get30DayStatistics = () => {
      return axios.get("/ebook-snapshot/30-statistics")
          .then(resp => {
            const json = resp.data;
            if (json.success) {
              statistics30day.value = json.content;
            } else {
              message.error(json.message);
            }
          })
    }



    const init30DayEcharts = (list: any) => {
      // 基于准备好的dom，初始化echarts实例
      const myChart = echarts.init(document.getElementById('main'));

      const xAxis = [];
      const seriesView = [];
      const seriesVote = [];
      for (let i = 0; i < list.length; i++) {
        const record = list[i];
        xAxis.push(record.date);
        seriesView.push(record.viewIncrease);
        seriesVote.push(record.voteIncrease);
      }

      // 指定图表的配置项和数据
      const option = {
        title: {
          text: '30-day trend'
        },
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          data: ['Daily change in views', 'Daily change in likes']
        },
        grid: {
          left: '1%',
          right: '3%',
          bottom: '3%',
          containLabel: true
        },
        toolbox: {
          feature: {
            saveAsImage: {}
          }
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: xAxis
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            name: 'Daily change in views',
            type: 'line',
            // stack: '总量', 不堆叠
            data: seriesView,
            smooth: true
          },
          {
            name: 'Daily change in likes',
            type: 'line',
            // stack: '总量', 不堆叠
            data: seriesVote,
            smooth: true
          }
        ]
      };

      // 使用刚指定的配置项和数据显示图表。
      myChart.setOption(option);
    };



    onMounted(() => {
      getStatistics();
      get30DayStatistics().then(() => {
        init30DayEcharts(statistics30day.value);
      });
    })

    return {
      statistics,
      todayViewIncreaseRateNA
    }
  }
});
</script>