package topic;

import org.apache.lucene.benchmark.quality.trec.TrecTopicsReader;

import java.util.Objects;

/**
 * Java POJO representing each topic provided by LongEval. Each topic has a number ("num") and a title
 * ("title")
 *
 * We had to create this class and {@link LongEvalTopicReader} because {@link TrecTopicsReader} is not processing
 * properly the topics provided by LongEval.
 *
 * @version 1.00
 * @since 1.00
 */
public class LongEvalTopic {

    private String num;
    private String title;

    /**
     * Create an instance of a LongEval topic.
     */
    public LongEvalTopic()
    {
        this.num = null;
        this.title = null;
    }

    /**
     * Create an instance of a LongEval topic.
     *
     * @param num topic identifier/number.
     * @param title topic/query title (only text given).
     */
    public LongEvalTopic(String num, String title)
    {
        this.num = num;
        this.title = title;
    }

    /**
     * Get the number of the topic.
     *
     * @return number of the topic.
     */
    public String getNum() {
        return num;
    }

    /**
     * Get the title of the topic.
     *
     * @return title of the topic.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set the number of the topic.
     *
     * @param num new number of the topic.
     */
    public void setNum(String num) {
        this.num = num;
    }

    /**
     * Set the title of the topic.
     *
     * @param title new title of the topic
     */
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LongEvalTopic that = (LongEvalTopic) o;
        return num.equals(that.num);
    }

    @Override
    public int hashCode() {
        return Objects.hash(num);
    }

    @Override
    public String toString() {
        return "LongEvalTopic(" +
                "num='" + num + '\'' +
                ", title='" + title + '\'' +
                ')';
    }
}
