


/* Apache UIMA v3 - First created by JCasGen Fri Mar 13 17:19:53 CET 2026 */

package nlp.types;


import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;

import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.impl.TypeSystemImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;


import org.apache.uima.jcas.tcas.Annotation;


/** Typ System für Reden
 * Updated by JCasGen Fri Mar 13 17:19:53 CET 2026
 * XML source: src/main/resources/desc/type/TypeSystem.xml
 * @generated */
public class PlenarSpeech extends Annotation {

  /** @generated
   * @ordered
   */
  @SuppressWarnings ("hiding")
  public final static String _TypeName = "nlp.types.PlenarSpeech";

  /** @generated
   * @ordered
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(PlenarSpeech.class);
  /** @generated
   * @ordered
   */
  @SuppressWarnings ("hiding")
  public final static int type = typeIndexID;
  /** @generated
   * @return index of the type
   */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}


  /* *******************
   *   Feature Offsets *
   * *******************/

  public final static String _FeatName_speechId = "speechId";
  public final static String _FeatName_speakerName = "speakerName";
  public final static String _FeatName_fraction = "fraction";


  /* Feature Adjusted Offsets */
  private final static CallSite _FC_speechId = TypeSystemImpl.createCallSite(PlenarSpeech.class, "speechId");
  private final static MethodHandle _FH_speechId = _FC_speechId.dynamicInvoker();
  private final static CallSite _FC_speakerName = TypeSystemImpl.createCallSite(PlenarSpeech.class, "speakerName");
  private final static MethodHandle _FH_speakerName = _FC_speakerName.dynamicInvoker();
  private final static CallSite _FC_fraction = TypeSystemImpl.createCallSite(PlenarSpeech.class, "fraction");
  private final static MethodHandle _FH_fraction = _FC_fraction.dynamicInvoker();


  /** Never called.  Disable default constructor
   * @generated */
  @Deprecated
  @SuppressWarnings ("deprecation")
  protected PlenarSpeech() {/* intentionally empty block */}

  /** Internal - constructor used by generator
   * @generated
   * @param casImpl the CAS this Feature Structure belongs to
   * @param type the type of this Feature Structure
   */
  public PlenarSpeech(TypeImpl type, CASImpl casImpl) {
    super(type, casImpl);
    readObject();
  }

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   */
  public PlenarSpeech(JCas jcas) {
    super(jcas);
    readObject();
  }


  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA
  */
  public PlenarSpeech(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }

  /**
   * <!-- begin-user-doc -->
   * Write your own initialization here
   * <!-- end-user-doc -->
   *
   * @generated modifiable
   */
  private void readObject() {/*default - does nothing empty block */}



  //*--------------*
  //* Feature: speechId

  /** getter for speechId - gets
   * @generated
   * @return value of the feature
   */
  public String getSpeechId() {
    return _getStringValueNc(wrapGetIntCatchException(_FH_speechId));
  }

  /** setter for speechId - sets
   * @generated
   * @param v value to set into the feature
   */
  public void setSpeechId(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_speechId), v);
  }



  //*--------------*
  //* Feature: speakerName

  /** getter for speakerName - gets
   * @generated
   * @return value of the feature
   */
  public String getSpeakerName() {
    return _getStringValueNc(wrapGetIntCatchException(_FH_speakerName));
  }

  /** setter for speakerName - sets
   * @generated
   * @param v value to set into the feature
   */
  public void setSpeakerName(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_speakerName), v);
  }



  //*--------------*
  //* Feature: fraction

  /** getter for fraction - gets
   * @generated
   * @return value of the feature
   */
  public String getFraction() {
    return _getStringValueNc(wrapGetIntCatchException(_FH_fraction));
  }

  /** setter for fraction - sets
   * @generated
   * @param v value to set into the feature
   */
  public void setFraction(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_fraction), v);
  }

  }
