package i.solonin.configmanager.model.template;

import i.solonin.configmanager.model.check.CheckingResult;
import i.solonin.configmanager.model.DBId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

@Getter
@Setter
@Entity(name = "Checking_Result_Divergence")
@NoArgsConstructor
public class Divergence extends DBId {
    @ManyToOne
    @JoinColumn(name = "checkingResultId")
    private CheckingResult checkingResult;

    private int templateLine;
    @Transient
    private String value;
    private boolean isDirective;

    public Divergence(int templateLine, String value) {
        this.templateLine = templateLine;
        this.value = value;
    }

    public Divergence(int templateLine, String value, boolean isDirective) {
        this.templateLine = templateLine;
        this.value = value;
        this.isDirective = isDirective;
    }
}
